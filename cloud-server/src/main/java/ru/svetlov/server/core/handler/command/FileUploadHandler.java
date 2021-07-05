package ru.svetlov.server.core.handler.command;

import ru.svetlov.domain.command.*;
import ru.svetlov.domain.command.base.Commands;
import ru.svetlov.domain.command.base.GenericCommand;
import ru.svetlov.domain.command.base.annotations.ACommandHandler;
import ru.svetlov.server.core.common.UserContext;
import ru.svetlov.server.service.file.FileUploadService;

import java.util.HashMap;
import java.util.Map;

@ACommandHandler(command = Commands.REPLY_UPLOAD_FILE)
public class FileUploadHandler implements CommandHandler {
    private static final int RETRY_COUNT = 3;
    private final FileUploadService uploadHandler;
    private final Map<UserContext, Map<Integer, Integer>> retriesMap;

    public FileUploadHandler(FileUploadService uploader) {
        this.uploadHandler = uploader;
        retriesMap = new HashMap<>();
    }

    @Override
    public GenericCommand process(GenericCommand command, UserContext context) {
        UploadReply reply = (UploadReply) command;
        int retry =  addRequest(context, reply.getRequestId());
        byte[] file = (byte[]) reply.getParameters()[0];
        if (uploadHandler.upload(context, reply.getRequestId(), file)) {
            clearRequest(context, reply.getRequestId());
            return new UploadDoneReply(reply.getReplyId() + 1, reply.getRequestId());
        }
        if (retry == RETRY_COUNT) return new RequestInvalidReply(reply.getReplyId() + 1, reply.getRequestId());
        return new UploadRetryReply(reply.getReplyId() + 1, reply.getRequestId());
    }

    private int addRequest(UserContext context, int requestId) {
        if (!retriesMap.containsKey(context))
            retriesMap.put(context, new HashMap<>());
        retriesMap.get(context).merge(requestId, 1, Integer::sum);
        return retriesMap.get(context).get(requestId);
    }

    private void clearRequest(UserContext context, int requestId) {
        if (!retriesMap.containsKey(context)) return;
        retriesMap.get(context).remove(requestId);
    }
}
