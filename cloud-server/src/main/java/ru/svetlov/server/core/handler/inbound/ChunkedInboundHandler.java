package ru.svetlov.server.core.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.domain.command.UploadChunksRequest;
import ru.svetlov.server.core.common.UserContext;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.file.FileUploadService;

public class ChunkedInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LogManager.getLogger();
    private final FileUploadService uploader;
    private final UserContext context;
    private final UploadChunksRequest request;
    private long bytesReceived;

    public ChunkedInboundHandler(FileUploadService uploader, UserContext context, UploadChunksRequest request) {
        this.uploader = uploader;
        this.context = context;
        this.request = request;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            int chunkSize = buf.readableBytes();

            if (uploader.upload(context, request.getRequestId(), ByteBufUtil.getBytes(buf))) {
                bytesReceived += chunkSize;
            }
        } finally {
            buf.release();
        }
        printReport(bytesReceived);
        if (bytesReceived < request.getFileSize()) return;

        ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null))); // TODO: убрать в конфигуратор пайплайна
        ctx.pipeline().remove(ChunkedWriteHandler.class);
        ctx.pipeline().replace(ChunkedInboundHandler.class, "", new InboundRequestHandler(Factory.getInstance().getCommandPool()));
    }

    private StringBuilder sb = new StringBuilder();

    private void printReport(long bytesReceived) {
        sb.setLength(0);
        sb.append("Received: ").append(bytesReceived).append(" / ").append(request.getFileSize());
        System.out.println(sb);
    }
}
