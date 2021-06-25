package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.ReplyCommand;

public class RequestInvalidReply extends ReplyCommand {
    private final static String COMMAND = "invalid_request";

    public RequestInvalidReply(int replyId, int requestId) {
        super(replyId, requestId, COMMAND, new Object[]{"Invalid request"});
    }
}
