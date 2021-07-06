package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginReply extends ReplyCommand {

    public LoginReply(int requestId, Object[] parameters) {
        super(requestId, CommandType.LOGIN_REPLY, parameters);
    }
}
