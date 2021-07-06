package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadDoneReply extends ReplyCommand {
    public UploadDoneReply(int requestId) {
        super(requestId, CommandType.REPLY_UPLOAD_DONE, new Object[]{"upload successful"});
    }
}
