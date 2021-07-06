package integration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.svetlov.domain.command.FileListRequest;
import ru.svetlov.domain.command.base.GenericCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NettyStressTest {
    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private NioEventLoopGroup loopGroup;

    public void startClient() {
        try {
            loopGroup = new NioEventLoopGroup(1);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ReadConsoleService(),
                                    new PrintConsoleService()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            //readInput(future.channel());
            sendCommands(future.channel());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Клиент останавливается с ошибкой " + e.getMessage());
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    private volatile int counter = 0;

    private class PrintConsoleService extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //System.out.println(msg);
            super.channelRead(ctx, msg);
            if (++counter == REQUESTS) ctx.writeAndFlush("exit");
        }
    }

    private class ReadConsoleService extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            //System.out.println("Read console service fired: " + msg);
            if (msg.equals("exit")) {
                System.out.println("exiting");
                ctx.channel().close();
            }
            super.write(ctx, msg, promise);
        }
    }

    private boolean exit = false;

    private void readInput(Channel channel) {
        new Thread(() -> {
            System.out.println("Читаем ввод...");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (!exit) {
                    String msg = reader.readLine();
                    if (msg.equals("exit"))
                        exit = true;
                    channel.writeAndFlush(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                exit = true;
            }
        }).start();
    }

    private static List<Thread> threads = new ArrayList<>(1000);

    private static final int THREADS = 24;
    private static final int REQUESTS = 32000 / THREADS;
    private static final int FLOOD_RATE = 10000 / THREADS;
    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++)
            threads.add(
                    new Thread(() -> {
                        NettyStressTest nettyTest = new NettyStressTest();
                        nettyTest.startClient();
                    }));
        long start = System.currentTimeMillis();
        System.out.println("starting...");
        for (Thread t : threads)
            t.start();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        StringBuilder sb =  new StringBuilder("Completed ")
                .append(REQUESTS * THREADS)
                .append(" requests @")
                .append(FLOOD_RATE * THREADS)
                .append(" rps in ")
                .append(System.currentTimeMillis() - start).append(" ms");
        System.out.println(sb);

    }

    private volatile int pid = (new Random()).nextInt(100)*1000+100000;

    private void sendCommands(Channel channel) {
        for (int i = 0; i < REQUESTS; i++) {
            GenericCommand testCommand = new FileListRequest(1, "");
            testCommand.setParameters(new Object[]{pid + i});
            channel.writeAndFlush(testCommand);
            sleep(1000 / FLOOD_RATE);
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
