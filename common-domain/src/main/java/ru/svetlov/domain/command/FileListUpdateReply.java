package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.ReplyCommand;

public class FileListUpdateReply extends ReplyCommand {

    public FileListUpdateReply(int requestId, String pathRoot, String fileList) {
        super(requestId, CommandType.LIST_FILES_UPDATE, new Object[]{pathRoot, fileList});
    }
}
