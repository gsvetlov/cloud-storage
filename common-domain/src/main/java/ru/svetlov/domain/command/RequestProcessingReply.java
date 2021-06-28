package ru.svetlov.domain.command;

import lombok.Getter;
import lombok.Setter;
import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

@Getter
@Setter
public class RequestProcessingReply extends ReplyCommand {

    public RequestProcessingReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.REQUEST_PROCESSING, new Object[]{"processing request..."});
    }
}
