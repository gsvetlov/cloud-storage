package ru.svetlov.server.core.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.svetlov.domain.command.LoginRequest;
import ru.svetlov.domain.command.LoginRequiredReply;
import ru.svetlov.domain.command.RequestProcessingReply;
import ru.svetlov.domain.command.base.RequestCommand;

public class AuthorizationInboundHandler extends SimpleChannelInboundHandler<RequestCommand> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestCommand request) throws Exception {
        if (request instanceof LoginRequest){
            ctx.fireChannelRead(request);
            ctx.writeAndFlush(new RequestProcessingReply(1, request.getRequestId()));
        }
        else {
            ctx.writeAndFlush(new LoginRequiredReply(1, request.getRequestId()));
        }
    }
}
