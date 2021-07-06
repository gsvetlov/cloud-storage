package ru.svetlov.server.core.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultExceptionHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LogManager.getLogger();
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Channel exception raised");
        log.throwing(cause);
        ctx.flush();
        ctx.close();
    }
}
