package ru.svetlov.domain.command;

import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.RequestCommand;

public class UploadRequest extends RequestCommand {

    public UploadRequest(int requestId, String path, String filename, long size) {
        super(requestId, Commands.REQUEST_FILE_UPLOAD, new Object[]{path, filename, size});
    }
}
