package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.ReplyCommand;

import java.util.List;

public class FileListUpdateReply extends ReplyCommand {

    public FileListUpdateReply(int replyId, int requestId, String pathRoot, String fileList) {
        super(replyId, requestId, Commands.LIST_FILES_UPDATE, new Object[]{pathRoot, fileList});
    }
}
