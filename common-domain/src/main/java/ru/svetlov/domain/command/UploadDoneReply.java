package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadDoneReply extends ReplyCommand {
    public UploadDoneReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.REPLY_UPLOAD_DONE, new Object[]{"upload successful"});
    }
}
