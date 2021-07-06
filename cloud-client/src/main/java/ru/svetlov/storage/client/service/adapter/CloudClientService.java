package ru.svetlov.storage.client.service.adapter;

import javafx.scene.control.TreeItem;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.common.Callback;
import ru.svetlov.storage.client.common.BiCallback;

import java.util.List;

public interface CloudClientService {
    void shutdown();
    void login(String host, String port, String username, String password);
    void listLocal(TreeItem<FileStructureInfo> item);
    void uploadFile(FileStructureInfo file);
    void listRemote(TreeItem<FileStructureInfo> item);
    void downloadFile(FileStructureInfo file);
    void setLoginEventHandler(Callback<String> callback);
    void setUploadEventHandler(Callback<String> callback);
    void setDownloadEventHandler(Callback<String> callback);
    void setListLocalDirectoryEventHandler(BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
    void setListRemoteDirectoryEventHandler(BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
}
