package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.RequestCommand;

public class FileListRequest extends RequestCommand {

    public FileListRequest(int requestId, String path) {
        super(requestId, CommandType.LIST_FILES, new Object[] { path });
    }
}
