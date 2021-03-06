package ru.svetlov.server.core.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.server.core.CloudServerService;
import ru.svetlov.server.core.handler.inbound.*;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.configuration.Configuration;

public class NettyCoreServer implements CloudServerService {
    private static final Logger log = LogManager.getLogger();
    private static final NettyCoreServer instance;

    static {
        instance = new NettyCoreServer();
    }

    public static CloudServerService getInstance() {
        return instance;
    }

    private NettyCoreServer() {
        configuration = Factory.getInstance().getConfiguration();
    }

    private final Configuration configuration;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private EventExecutorGroup eventExecutors;
    private static final int EVENT_EXECUTOR_THREADS = 4;

    @Override
    public void startServer() {
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            eventExecutors = new DefaultEventExecutorGroup(EVENT_EXECUTOR_THREADS);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new LoggingHandler("nettyLogger", LogLevel.TRACE),
                                    new DefaultExceptionHandler(),
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new AuthorizationInboundHandler()
//                                    new InboundRequestHandler(Factory.getInstance().getCommandPool()) // TODO: ???????????????? ??????????-???????????????????????? ??????????????????
                            ).addLast(eventExecutors,
                                    new AuthenticationHandler(Factory.getInstance().getAuthenticationProvider())

                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(getPort()).sync();
            log.info("???????????? ??????????????");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.throwing(Level.ERROR, e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            eventExecutors.shutdownGracefully();
            log.info("???????????? ????????????????????");
        }
    }

    private int getPort() {
        try {
            return Integer.parseInt(configuration.get("port"));
        } catch (NumberFormatException e) {
            log.throwing(e);
            throw e;
        }
    }
}
