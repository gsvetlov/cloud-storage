package ru.svetlov.storage.client.service.router;

import ru.svetlov.domain.file.FileStructureInfo;

import java.io.IOException;
import java.util.List;

public interface RemoteStorageService {
    RemoteOperationResult connect(String host, int port) throws IOException;
    void disconnect();

    RemoteOperationResult login(String username, String password);

    List<FileStructureInfo> listFiles(String path);

    RemoteOperationResult postFile(FileStructureInfo file);
    RemoteOperationResult getFile(FileStructureInfo file);
}
