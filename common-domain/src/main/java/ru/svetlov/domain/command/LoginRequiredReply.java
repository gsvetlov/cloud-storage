package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginRequiredReply extends ReplyCommand {

    public LoginRequiredReply(int requestId) {
        super(requestId, CommandType.LOGIN_REQUIRED, new Object[]{"Authentication required. Command dropped."});
    }
}
