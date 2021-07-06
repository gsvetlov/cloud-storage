package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadChunksReply extends ReplyCommand {

    public UploadChunksReply(int requestId, int size) {
        super(requestId, CommandType.REPLY_UPLOAD_CHUNKS, new Object[]{size});
    }
}
