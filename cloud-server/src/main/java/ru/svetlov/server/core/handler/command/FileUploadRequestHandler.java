package ru.svetlov.server.core.handler.command;

import ru.svetlov.domain.command.RequestInvalidReply;
import ru.svetlov.domain.command.UploadProceedReply;
import ru.svetlov.domain.command.UploadRequest;
import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.UploadChunksReply;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.core.domain.UserContext;
import ru.svetlov.server.service.transfer.FileUploadService;

@ACommandHandler(command = CommandType.REQUEST_FILE_UPLOAD)
public class FileUploadRequestHandler implements CommandHandler {
    private static final int MAX_OBJECT_SIZE = 1048500;
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

        if (size > MAX_OBJECT_SIZE) return new UploadChunksReply(request.getRequestId(), MAX_OBJECT_SIZE);

        if (uploadHandler.prepare(context, request.getRequestId(), path, filename, size))
            return new UploadProceedReply(request.getRequestId());
        return new RequestInvalidReply(request.getRequestId());
    }
}
