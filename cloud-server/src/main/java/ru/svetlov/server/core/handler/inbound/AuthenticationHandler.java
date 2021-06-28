package ru.svetlov.server.core.handler.inbound;

import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.svetlov.domain.command.LoginReply;
import ru.svetlov.domain.command.LoginRequest;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.jdbc.AuthenticationResult;


public class AuthenticationHandler extends SimpleChannelInboundHandler<LoginRequest> {

    private final AuthenticationProvider service;

    public AuthenticationHandler(AuthenticationProvider service) {
        super();
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequest command) throws Exception {
        Object[] parameters = command.getParameters(); // TODO: replace with safe and validated data
        String login = (String) parameters[0];
        String pass = (String) parameters[1];
        AuthenticationResult authenticate = service.authenticate(login, pass);
        if (authenticate.isSuccess()) {
            ctx.pipeline().replace(AuthorizationInboundHandler.class,
                    "",
                    new InboundRequestHandler(Factory.getInstance().getCommandPool()));
            ctx.pipeline().remove(AuthenticationHandler.class);
        }
        ObjectWriter writer = Factory.getInstance().getJsonMapProvider().getWriter();
        ctx.writeAndFlush(new LoginReply(
                1,
                command.getRequestId(),
                new Object[]{writer.writeValueAsString(authenticate)}));
    }
}
