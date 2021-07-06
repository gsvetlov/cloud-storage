package ru.svetlov.server.core.handler.command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.svetlov.domain.command.RequestInvalidReply;
import ru.svetlov.domain.command.UploadChunksRequest;
import ru.svetlov.domain.command.UploadProceedReply;
import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.core.domain.UserContext;
import ru.svetlov.server.core.handler.inbound.ChunkedInboundHandler;
import ru.svetlov.server.core.handler.inbound.InboundRequestHandler;
import ru.svetlov.server.service.transfer.FileUploadService;

@ACommandHandler(command = CommandType.REQUEST_UPLOAD_CHUNKS)
public class ChunkedFileUploadRequestHandler implements CommandHandler{
    private FileUploadService uploader;
    public ChunkedFileUploadRequestHandler(FileUploadService uploader){
        this.uploader = uploader;
    }
    @Override
    public GenericCommand process(GenericCommand command, UserContext context) {
        UploadChunksRequest request = (UploadChunksRequest)command;
        String path = (String) request.getParameters()[0];
        String filename = (String) request.getParameters()[1];
        long size = (long) request.getParameters()[2];
        if (uploader.prepare(context, request.getRequestId(), path, filename, size)) {
            preparePipeline(context.getChannelHandlerContext(), context, request);
            return new UploadProceedReply(request.getRequestId());
        }
        return new RequestInvalidReply(request.getRequestId());
    }

    private void preparePipeline(ChannelHandlerContext ctx, UserContext userContext, UploadChunksRequest request) {
        ctx.pipeline().addFirst(
                new ChunkedWriteHandler(),
                new ChunkedInboundHandler(uploader, userContext, request));
        ctx.pipeline().remove(ObjectDecoder.class);
        ctx.pipeline().remove(InboundRequestHandler.class);
    }
}
