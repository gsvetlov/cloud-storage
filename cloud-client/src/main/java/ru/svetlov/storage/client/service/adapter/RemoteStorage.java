package ru.svetlov.storage.client.service.adapter;

import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.service.router.RemoteOperationResult;

import java.io.IOException;
import java.util.List;

public interface RemoteStorage {
    RemoteOperationResult connect(String host, int port) throws IOException;
    void disconnect();
    RemoteOperationResult login(String username, String password);
    List<FileStructureInfo> listFiles(String path);
    RemoteOperationResult postFile(FileStructureInfo file);
    RemoteOperationResult getFile(FileStructureInfo file);
}
