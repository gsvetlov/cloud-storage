package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadRetryReply extends ReplyCommand {
    public UploadRetryReply(int requestId) {
        super(requestId, CommandType.REPLY_UPLOAD_RETRY, new Object[]{"upload failed. retry..."});
    }
}
