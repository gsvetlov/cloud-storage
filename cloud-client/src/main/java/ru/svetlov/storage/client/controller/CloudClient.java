package ru.svetlov.storage.client.controller;

import javafx.scene.control.TreeItem;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.common.Callback;
import ru.svetlov.storage.client.common.Callback2;

import java.util.List;

public interface CloudClient {
    void shutdown();
    void login(String host, String port, String username, String password);
    void listLocalDirectory(TreeItem<FileStructureInfo> item);
    void uploadFile(FileStructureInfo file);
    void listRemoteDirectory(TreeItem<FileStructureInfo> item);
    void downloadFile(FileStructureInfo file);
    void setLoginEventHandler(Callback<String> callback);
    void setUploadEventHandler(Callback<String> callback);
    void setDownloadEventHandler(Callback<String> callback);
    void setListLocalDirectoryEventHandler(Callback2<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
    void setListRemoteDirectoryEventHandler(Callback2<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
}
