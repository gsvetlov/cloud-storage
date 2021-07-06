package ru.svetlov.domain.command;

import lombok.Getter;
import lombok.Setter;
import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

@Getter
@Setter
public class RequestProcessingReply extends ReplyCommand {

    public RequestProcessingReply(int requestId) {
        super(requestId, CommandType.REQUEST_PROCESSING, new Object[]{"processing request..."});
    }
}
