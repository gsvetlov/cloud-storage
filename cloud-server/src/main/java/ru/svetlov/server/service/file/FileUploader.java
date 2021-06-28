package ru.svetlov.server.service.file;

import ru.svetlov.server.core.common.UserContext;

public interface FileUploader {
    boolean prepare(UserContext context, int requestId, String path, String filename, long fileSize);

    boolean upload(UserContext context, int requestId, byte[] file);
}
