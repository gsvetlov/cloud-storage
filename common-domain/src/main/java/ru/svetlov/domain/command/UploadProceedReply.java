package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadProceedReply extends ReplyCommand {

    public UploadProceedReply(int replyId, int requestId) {
        super(replyId, requestId, Commands.REPLY_UPLOAD_PROCEED, new Object[]{"Ready to upload"});
    }
}
