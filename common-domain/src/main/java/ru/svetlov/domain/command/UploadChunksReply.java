package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

public class UploadChunksReply extends ReplyCommand {

    public UploadChunksReply(int replyId, int requestId, int size) {
        super(replyId, requestId, Commands.REPLY_UPLOAD_CHUNKS, new Object[]{size});
    }
}
