package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginReply extends ReplyCommand {
    private static final String COMMAND = "login_reply";
    public LoginReply(int replyId, int requestId, Object[] parameters) {
        super(replyId, requestId, COMMAND, parameters);
    }
}
