package ru.svetlov.storage.client.service.network.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.domain.command.base.ReplyCommand;
import ru.svetlov.domain.command.base.RequestCommand;
import ru.svetlov.storage.client.common.Callback;
import ru.svetlov.storage.client.service.network.NetworkClient;

import java.io.IOException;

public class NetClient implements NetworkClient {
    private static final Logger log = LogManager.getLogger();

    private boolean connected;
    private final Bootstrap bootstrap = new Bootstrap();
    private Callback<ReplyCommand> replyHandler;
    private ChannelFuture future;
    private NioEventLoopGroup loopGroup;

    public NetClient() {
        startClient();
    }

    public void startClient() {
        loopGroup = new NioEventLoopGroup();
        bootstrap
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new InboundReplyHandler()
                        );
                    }
                });
    }

    @Override
    public boolean connect(String host, int port) throws IOException {
        if (connected) return true;
        try {
            future = bootstrap.connect(host, port).sync();
            log.info("Клиент запущен");
        } catch (Exception e) {
            connected = false;
            log.warn("Клиент останавливается с ошибкой " + e.getMessage());
            disconnect();
            throw new IOException(e);
        }
        connected = future.channel().isOpen();
        return connected;
    }

    @Override
    public void disconnect() {
        try {
            if (future != null)
                future.channel().close();
        } catch (Exception e) {
            connected = false;
            log.warn("Клиент останавливается с ошибкой " + e.getMessage());
        } finally {
            if (loopGroup != null)
                loopGroup.shutdownGracefully();
            log.info("Клиент остановлен");
        }
    }

    @Override
    public void post(RequestCommand command) {
        future.channel().writeAndFlush(command);
    }

    @Override
    public void setReplyHandler(Callback<ReplyCommand> callback) {
        this.replyHandler = callback;
    }

    @Override
    public void postRequest(RequestCommand request, Callback<ReplyCommand> replyCallback) {
        future.channel().writeAndFlush(request);
        log.trace(request);
        log.trace(replyCallback);
        this.replyHandler = replyCallback;
    }

    private class InboundReplyHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            log.trace(o);
            replyHandler.call((ReplyCommand)o);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
            log.warn("Exception caught");
            log.throwing(throwable);
            if (throwable instanceof TooLongFrameException) return;
            channelHandlerContext.close();
        }
    }
}
