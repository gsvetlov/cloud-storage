package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class RequestInvalidReply extends ReplyCommand {

    public RequestInvalidReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.INVALID_REQUEST, new Object[]{"Invalid request"});
    }
}
