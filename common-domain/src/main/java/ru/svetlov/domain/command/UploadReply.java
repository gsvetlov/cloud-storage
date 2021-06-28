package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadReply extends ReplyCommand {
    public UploadReply(int replyId, int requestId, byte[] file) {
        super(replyId, requestId, Commands.REPLY_UPLOAD_FILE, new Object[]{file});
    }
}
