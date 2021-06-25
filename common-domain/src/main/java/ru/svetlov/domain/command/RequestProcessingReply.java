package ru.svetlov.domain.command;

import lombok.Getter;
import lombok.Setter;
import ru.svetlov.domain.command.base.ReplyCommand;

@Getter
@Setter
public class RequestProcessingReply extends ReplyCommand {
    private final static String COMMAND = "processing";

    public RequestProcessingReply(int replyId, int requestId) {
        super(replyId, requestId, COMMAND, new Object[]{"processing request..."});
    }
}
