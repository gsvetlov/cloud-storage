package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginRequiredReply extends ReplyCommand {

    public LoginRequiredReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.LOGIN_REQUIRED, new Object[]{"Authentication required. Command dropped."});
    }
}
