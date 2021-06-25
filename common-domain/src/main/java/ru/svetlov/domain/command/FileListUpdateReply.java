package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.ReplyCommand;

import java.util.List;

public class FileListUpdateReply extends ReplyCommand {
    private static final String COMMAND = "file_list_update";

    public FileListUpdateReply(int replyId, int requestId, String pathRoot, String fileList) {
        super(replyId, requestId, COMMAND, new Object[]{pathRoot, fileList});
    }
}
