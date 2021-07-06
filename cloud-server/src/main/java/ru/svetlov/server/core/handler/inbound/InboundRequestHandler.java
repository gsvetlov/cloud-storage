package ru.svetlov.server.core.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.svetlov.domain.command.RequestInvalidReply;
import ru.svetlov.domain.command.RequestProcessingReply;
import ru.svetlov.domain.command.base.RequestCommand;
import ru.svetlov.server.core.domain.UserContext;
import ru.svetlov.server.core.handler.command.CommandHandler;
import ru.svetlov.server.service.pool.CommandPool;

public class InboundRequestHandler extends SimpleChannelInboundHandler<RequestCommand> {

    private final CommandPool pool;

    public InboundRequestHandler(CommandPool pool) {
        this.pool = pool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestCommand request) throws Exception {
        CommandHandler handler = pool.getHandler(request);
        if (handler == null)
            ctx.writeAndFlush(new RequestInvalidReply(request.getRequestId()));
        else {
            ctx.writeAndFlush(new RequestProcessingReply(request.getRequestId()));
            // TODO: юзер-контекст нужно создавать правильно!
            ctx.writeAndFlush(handler.process(request, new UserContext("testUser", 1423, "c:/temp", ctx)));
        }
    }
}
