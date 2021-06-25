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
import ru.svetlov.domain.command.LoginReply;
import ru.svetlov.domain.command.LoginRequest;
import ru.svetlov.domain.command.base.ReplyCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class CloudCommandTest {
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
                                    new GetCommandService(),
                                    new PrintConsoleService(),
                                    new ReadConsoleService()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Клиент запущен");
            readInput(future.channel());
            sendCommand(future.channel());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Клиент останавливается с ошибкой " + e.getMessage());
        } finally {
            loopGroup.shutdownGracefully();
            System.out.println("Клиент остановлен");
        }
    }

    private static class PrintConsoleService extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            super.channelRead(ctx, msg);
        }
    }

    private static class ReadConsoleService extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("Read console service fired: " + msg);
            if (msg.equals("exit")) {
                System.out.println("exiting");
                ctx.channel().close();
            }
            super.write(ctx, msg, promise);
        }
    }

    private static class GetCommandService extends SimpleChannelInboundHandler<ReplyCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ReplyCommand rep) throws Exception {
            System.out.println("Reply received");
            StringBuilder sb = new StringBuilder(rep.getCommand());
            sb.append(" request Id: ").append(rep.getRequestId())
                    .append("; replyId: ").append(rep.getReplyId()).append("\n");
            for (Object o : rep.getParameters()) {
                sb.append(o).append("\n");
                if (o instanceof String){
                    loginOk = ((String) o).contains("\"success\":true");
                }
            }
            System.out.println(sb);
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

    private int pid = (new Random()).nextInt(100) + 100;

    private static boolean loginOk;

    private void sendCommand(Channel channel) {
        FileListRequest listRequest = new FileListRequest(pid, "temp");
        LoginRequest loginRequest = new LoginRequest(pid, "me", "mySecretPass");
        while (!loginOk) {
            channel.writeAndFlush(loginRequest);
            sleep(1000);
        }
        channel.writeAndFlush(listRequest);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CloudCommandTest commandTest = new CloudCommandTest();
        commandTest.startClient();
    }
}