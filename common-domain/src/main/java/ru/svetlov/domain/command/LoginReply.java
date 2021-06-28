package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginReply extends ReplyCommand {

    public LoginReply(int replyId, int requestId, Object[] parameters) {
        super(replyId, requestId, Commands.LOGIN_REPLY, parameters);
    }
}
