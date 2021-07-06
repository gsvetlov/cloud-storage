package ru.svetlov.server.core.handler.inbound;

import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.svetlov.domain.command.LoginReply;
import ru.svetlov.domain.command.LoginRequest;
import ru.svetlov.server.factory.Factory;
import ru.svetlov.server.service.jdbc.AuthenticationProvider;
import ru.svetlov.server.service.jdbc.domain.AuthenticationResult;

import java.sql.SQLException;


public class AuthenticationHandler extends SimpleChannelInboundHandler<LoginRequest> {
    private static final Logger log = LogManager.getLogger();
    private final AuthenticationProvider service;

    public AuthenticationHandler(AuthenticationProvider service) {
        super();
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequest command) throws Exception {
        Object[] parameters = command.getParameters(); // TODO: нужна валидация
        String login = (String) parameters[0];
        String pass = (String) parameters[1];
        AuthenticationResult authenticate = service.authenticate(login, pass);
        if (authenticate.isSuccess()) {
            ctx.pipeline().replace(AuthorizationInboundHandler.class, // TODO: убрать в конфигуратор пайплайна
                    "",
                    new InboundRequestHandler(Factory.getInstance().getCommandPool()));
            ctx.pipeline().remove(AuthenticationHandler.class);
        }
        ObjectWriter writer = Factory.getInstance().getJsonMapProvider().getWriter();
        ctx.writeAndFlush(new LoginReply(
                command.getRequestId(),
                new Object[]{writer.writeValueAsString(authenticate)}));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        try {
            service.connect();
            super.handlerAdded(ctx);
        } catch (SQLException e) {
            log.throwing(e);
            ctx.close();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        service.shutdown();
        super.handlerRemoved(ctx);
    }
}
