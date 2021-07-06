package ru.svetlov.domain.command;

import lombok.Getter;
import ru.svetlov.domain.command.base.CommandType;
import ru.svetlov.domain.command.base.RequestCommand;

@Getter
public class UploadChunksRequest extends RequestCommand {
    private final String path;
    private final String filename;
    private final long fileSize;
    private final int chunkSize;

    public UploadChunksRequest(int requestId, String path, String filename, long fileSize, int chunkSize) {
        super(requestId, CommandType.REQUEST_UPLOAD_CHUNKS, new Object[]{path, filename, fileSize, chunkSize});
        this.path = path;
        this.filename = filename;
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
    }
}
