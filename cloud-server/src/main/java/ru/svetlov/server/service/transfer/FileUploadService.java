package ru.svetlov.server.service.transfer;

import ru.svetlov.server.core.domain.UserContext;

public interface FileUploadService {
    boolean prepare(UserContext context, int requestId, String path, String filename, long fileSize);

    boolean upload(UserContext context, int requestId, byte[] file);
}
