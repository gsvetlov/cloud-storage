package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadProceedReply extends ReplyCommand {

    public UploadProceedReply(int requestId) {
        super(requestId, CommandType.REPLY_UPLOAD_PROCEED, new Object[]{"Ready to upload"});
    }
}
