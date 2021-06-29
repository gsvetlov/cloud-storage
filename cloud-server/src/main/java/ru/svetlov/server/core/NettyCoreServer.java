package ru.svetlov.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.svetlov.server.core.handler.inbound.*;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.jdbc.AuthenticationProvider;

public class NettyCoreServer implements CloudServerService {

    private static volatile NettyCoreServer _instance;
    private static final Object _lock = new Object();

    public static CloudServerService getInstance() {
        if (_instance != null) return _instance;
        synchronized (_lock) {
            if (_instance == null)
                _instance = new NettyCoreServer();
        }
        return _instance;
    }

    private NettyCoreServer() {
    }

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private EventExecutorGroup eventExecutors;

    @Override
    public void startServer() {
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(2);
            eventExecutors = new DefaultEventExecutorGroup(4);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new AuthorizationInboundHandler()
//                                    new InboundRequestHandler(Factory.getInstance().getCommandPool()) // TODO: добавить класс-конфигуратор пайплайна
                            ).addLast(eventExecutors,
                                    new AuthenticationHandler(getAuthenticationProvider())
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            System.out.println("Сервер запущен");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Сервер останавливается с ошибкой " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            eventExecutors.shutdownGracefully();
            System.out.println("Сервер остановлен");
        }
    }

    private AuthenticationProvider getAuthenticationProvider() {
        return (AuthenticationProvider) Factory.getInstance().getService(AuthenticationProvider.class)
                .orElseThrow(() -> new IllegalArgumentException("Can't locate AuthenticationProvider service"));
    }
}
