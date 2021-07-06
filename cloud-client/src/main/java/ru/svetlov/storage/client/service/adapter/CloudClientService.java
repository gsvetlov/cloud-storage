package ru.svetlov.storage.client.service.adapter;

import javafx.scene.control.TreeItem;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface CloudClientService {
    void shutdown();
    void login(String host, String port, String username, String password);
    void listLocal(TreeItem<FileStructureInfo> item);
    void uploadFile(FileStructureInfo file);
    void listRemote(TreeItem<FileStructureInfo> item);
    void downloadFile(FileStructureInfo file);
    void setLoginEventHandler(Consumer<String> callback);
    void setUploadEventHandler(Consumer<String> callback);
    void setDownloadEventHandler(Consumer<String> callback);
    void setListLocalDirectoryEventHandler(BiConsumer<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
    void setListRemoteDirectoryEventHandler(BiConsumer<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback);
}
