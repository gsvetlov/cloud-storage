package ru.svetlov.server.core.handler.command;

import ru.svetlov.domain.command.RequestInvalidReply;
import ru.svetlov.domain.command.UploadProceedReply;
import ru.svetlov.domain.command.UploadRequest;
import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.UploadChunksReply;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.core.common.UserContext;
import ru.svetlov.server.service.file.FileUploadService;

@ACommandHandler(command = Commands.REQUEST_FILE_UPLOAD)
public class FileUploadRequestHandler implements CommandHandler {
    private final FileUploadService uploadHandler;

    public FileUploadRequestHandler(FileUploadService uploader) {
        this.uploadHandler = uploader;
    }

    @Override
    public GenericCommand process(GenericCommand command, UserContext context) {
        UploadRequest request = (UploadRequest) command;
        String path = (String) request.getParameters()[0];
        String filename = (String) request.getParameters()[1];
        long size = (long) request.getParameters()[2];

        if (size > 1048500) return new UploadChunksReply(1, request.getRequestId(), 1048500);

        if (uploadHandler.prepare(context, request.getRequestId(), path, filename, size))
            return new UploadProceedReply(1, request.getRequestId());
        return new RequestInvalidReply(1, request.getRequestId());
    }
}
