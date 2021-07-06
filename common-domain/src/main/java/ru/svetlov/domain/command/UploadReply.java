package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadReply extends ReplyCommand {
    public UploadReply(int requestId, byte[] file) {
        super(requestId, CommandType.REPLY_UPLOAD_FILE, new Object[]{file});
    }
}
