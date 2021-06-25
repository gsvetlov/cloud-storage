package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.RequestCommand;

public class FileListRequest extends RequestCommand {
    private static final String COMMAND = "list_files";

    public FileListRequest(int requestId, String path) {
        super(requestId, COMMAND, new Object[] { path });
    }
}
