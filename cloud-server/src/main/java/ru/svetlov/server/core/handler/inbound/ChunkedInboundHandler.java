package ru.svetlov.server.core.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.svetlov.domain.command.UploadChunksRequest;
import ru.svetlov.server.core.common.UserContext;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.file.FileUploader;

public class ChunkedInboundHandler extends ChannelInboundHandlerAdapter {
    private final FileUploader uploader;
    private final UserContext context;
    private final UploadChunksRequest request;
    private long bytesReceived;
    public ChunkedInboundHandler(FileUploader uploader, UserContext context, UploadChunksRequest request) {
        this.uploader = uploader;
        this.context = context;
        this.request = request;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        int chunkSize = buf.readableBytes();
        if (uploader.upload(context, request.getRequestId(), ByteBufUtil.getBytes(buf)))
            bytesReceived += chunkSize;
        buf.release();
        printReport(bytesReceived);
        if (bytesReceived < request.getFileSize()) return;
        System.out.println("restoring pipeline");
        ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        ctx.pipeline().remove(ChunkedWriteHandler.class);
        ctx.pipeline().replace(ChunkedInboundHandler.class, "", new InboundRequestHandler(Factory.getInstance().getCommandPool()));
    }

    private StringBuilder sb = new StringBuilder();
    private void printReport(long bytesReceived){
        sb.setLength(0);
        sb.append("Received: ").append(bytesReceived).append(" / ").append(request.getFileSize());
        System.out.println(sb);
    }
}
