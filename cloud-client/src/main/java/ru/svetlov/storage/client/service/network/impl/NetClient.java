package ru.svetlov.storage.client.service.network.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.svetlov.domain.command.base.ReplyCommand;
import ru.svetlov.domain.command.base.RequestCommand;
import ru.svetlov.storage.client.common.Callback;
import ru.svetlov.storage.client.service.network.NetworkClient;

import java.io.IOException;

public class NetClient implements NetworkClient {

    private String host;
    private int port;
    private boolean connected;
    private final Bootstrap bootstrap = new Bootstrap();
    private Callback<ReplyCommand> replyHandler;
    private ChannelFuture future;
    private NioEventLoopGroup loopGroup;

    public NetClient() {
        startClient();
    }

    public void startClient() {
        loopGroup = new NioEventLoopGroup(1);
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
        this.host = host;
        this.port = port;
        try {
            future = bootstrap.connect(host, port).sync();
            System.out.println("Клиент запущен");
        } catch (Exception e) {
            connected = false;
            System.out.println("Клиент останавливается с ошибкой " + e.getMessage());
            disconnect();
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
            System.out.println("Клиент останавливается с ошибкой " + e.getMessage());
        } finally {
            if (loopGroup != null)
                loopGroup.shutdownGracefully();
            System.out.println("Клиент остановлен");
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

    private class InboundReplyHandler extends SimpleChannelInboundHandler<ReplyCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ReplyCommand replyCommand) throws Exception {
            replyHandler.call(replyCommand);
        }
    }
}
