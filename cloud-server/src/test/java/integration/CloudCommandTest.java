package integration;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import ru.svetlov.domain.command.*;
import ru.svetlov.domain.command.base.ReplyCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.*;
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
                if (o instanceof String) {
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

    private int pid = (new Random()).nextInt(900) + 100;

    private static boolean loginOk;
    private static boolean canProceed;

    private void sendCommand(Channel channel) {
        //listRequest(channel);
        loginSequence(channel);
        UploadRequest rq = new UploadRequest(pid, "./upload", "background" + pid + ".png", 817042);
        channel.writeAndFlush(rq);
        sleep(1000);
        byte[] file = readFileBytes("C:\\temp\\temp\\StarGame-res\\background01.png");
        UploadReply rp = new UploadReply(2, rq.getRequestId(), file);
        System.out.println("sending file...");
        channel.writeAndFlush(rp);
        sleep(2000);
        UploadRequest rq2 = new UploadRequest(pid * 2 + 1, "./upload", "gdx-texturepacker.jar", 7773477);
        channel.writeAndFlush(rq2);
        sleep(1000);

        canProceed = false;
        UploadChunksRequest rqChunks = new UploadChunksRequest(
                pid * 2 + 2,
                "./upload",
                "gdx-texturepacker" + pid + ".jar",
                7773477,
                50_000);
        channel.writeAndFlush(rqChunks);
        sleep(3000);
        System.out.println("sending file...");

        sendChunks(channel, Paths.get("C:\\temp\\temp\\StarGame-res\\gdx-texturepacker.jar"), 50_000);
        System.out.println("done...");
        while (!canProceed)
            sleep(1000);
        canProceed = false;
        UploadChunksRequest bigChunks = new UploadChunksRequest(
                pid * 3 + 3,
                "./upload",
                "CentOS-8.3.2011-x86_64-dvd1" + pid + ".iso",
                9264168960L,
                1_000_000);
        sleep(3000);

        //C:\vm\iso\CentOS-8.3.2011-x86_64-dvd1.iso  - size: 9Gb
//        System.out.println("sending big file...");
//        sendChunks(channel, Paths.get("C:\\vm\\iso\\CentOS-8.3.2011-x86_64-dvd1.iso"), 1_000_000);
//        while (!canProceed)
//            sleep(1000);
//        System.out.println("done...");
//        sleep(3000);
    }


    private byte[] readFileBytes(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendChunks(Channel channel, Path path, int buffSize) {
        ChunkedNioFile file = getFile(path, buffSize);
        if (file == null) return;
        System.out.println("replacing pipeline");
        channel.pipeline().addFirst(new ChunkedWriteHandler());
        channel.pipeline().remove(ObjectEncoder.class);
        ByteBuf buffer = null;
        try {
            System.out.println("sending...");
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(path.toFile(), buffSize));
            future.addListener((ChannelFutureListener) channelFuture -> restorePipeline(channel));
//            while (file.isEndOfInput()) {
//                buffer = file.readChunk(ByteBufAllocator.DEFAULT);
//                System.out.println("buffer have :" + buffer.readableBytes());
//                channel.writeAndFlush(buffer);
//            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (buffer != null) {
                buffer.release();
            }
        }

    }

    private void restorePipeline(Channel channel) {
        System.out.println("transfer done");
        channel.pipeline().remove(ChunkedWriteHandler.class);
        channel.pipeline().addFirst(new ObjectEncoder());
        canProceed = true;
        System.out.println("restoring pipeline");
    }

    private ChunkedNioFile getFile(Path path, int buffSize) {
        try {
            FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
            return new ChunkedNioFile(channel, buffSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void listRequest(Channel channel) {
        FileListRequest listRequest = new FileListRequest(pid, "temp");
        loginSequence(channel);
        channel.writeAndFlush(listRequest);
    }

    private void loginSequence(Channel channel) {
        LoginRequest loginRequest = new LoginRequest(pid, "me", "mySecretPass");
        while (!loginOk) {
            channel.writeAndFlush(loginRequest);
            sleep(1000);
        }
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