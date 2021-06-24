package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.ReplyCommand;

public class LoginRequiredReply extends ReplyCommand {
    private static final String COMMAND = "login_required";

    public LoginRequiredReply(int replyId, int requestId) {
        super(replyId, requestId, COMMAND, new Object[]{"Authentication required. Command dropped."});
    }
}
