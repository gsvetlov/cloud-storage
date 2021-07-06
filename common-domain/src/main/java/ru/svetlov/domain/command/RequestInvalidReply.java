package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class RequestInvalidReply extends ReplyCommand {

    public RequestInvalidReply(int requestId) {
        super(requestId, CommandType.INVALID_REQUEST, new Object[]{"Invalid request"});
    }
}
