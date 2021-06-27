package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadRetryReply extends ReplyCommand {
    public UploadRetryReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.REPLY_UPLOAD_RETRY, new Object[]{"upload failed. retry..."});
    }
}
