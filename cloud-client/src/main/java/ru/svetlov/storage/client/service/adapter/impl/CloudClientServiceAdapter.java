package ru.svetlov.storage.client.service.adapter.impl;

import javafx.scene.control.TreeItem;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.common.Callback;
import ru.svetlov.storage.client.common.BiCallback;
import ru.svetlov.storage.client.service.adapter.CloudClientService;
import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.domain.file.FileType;
import ru.svetlov.storage.client.service.router.RemoteOperationResult;
import ru.svetlov.storage.client.service.router.RemoteStorageService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CloudClientServiceAdapter implements CloudClientService {
    private Callback<String> loginHandler;
    private Callback<String> uploadHandler;
    private Callback<String> downloadHandler;
    private BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> remoteListHandler;
    private BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> localListHandler;

    private final FileViewService localView;
    private final RemoteStorageService remoteStorage;

    public CloudClientServiceAdapter(FileViewService localView, RemoteStorageService remoteStorage) {
        this.localView = localView;
        this.remoteStorage = remoteStorage;
    }

    @Override
    public void login(String host, String port, String username, String password) {
        RemoteOperationResult result = connect(host, port);
        if (result.isSuccess())
            result = remoteStorage.login(username, password);
        if (loginHandler != null)
            loginHandler.call(result.getMessage());
    }

    private RemoteOperationResult connect(String host, String port) {
        RemoteOperationResult result;
        try {
            result = remoteStorage.connect(host, Integer.parseInt(port));
        } catch (NumberFormatException | IOException ex) {
            result = new RemoteOperationResult(false, ex.getMessage());
        }
        return result;
    }

    @Override
    public void listLocal(TreeItem<FileStructureInfo> item) {
        listDirectory(item, false);
    }

    @Override
    public void listRemote(TreeItem<FileStructureInfo> item) {
        listDirectory(item, true);
    }

    private void listDirectory(TreeItem<FileStructureInfo> item, boolean remote) {
        FileType type = item.getValue().getType();
        if (type != FileType.DIRECTORY) return;

        String parent = item.getValue().getParent();
        String filename = item.getValue().getFilename();
        Path path = parent == null ? Paths.get(filename) : Paths.get(parent, filename).normalize();

        if (remote && remoteListHandler != null)
            remoteListHandler.call(item, remoteStorage.listFiles(path.toString()));
        if (!remote && localListHandler != null)
            localListHandler.call(item, localView.getListView(path));
    }

    @Override
    public void uploadFile(FileStructureInfo file) {
        RemoteOperationResult remoteOperationResult = remoteStorage.postFile(file);
        if (uploadHandler != null)
            uploadHandler.call(remoteOperationResult.isSuccess() ? "ok" : remoteOperationResult.getMessage());
    }

    @Override
    public void downloadFile(FileStructureInfo file) {
        RemoteOperationResult remoteOperationResult = remoteStorage.getFile(file);
        if (downloadHandler != null)
            downloadHandler.call(remoteOperationResult.isSuccess() ? "ok" : remoteOperationResult.getMessage());
    }

    @Override
    public void setLoginEventHandler(Callback<String> callback) {
        this.loginHandler = callback;
    }

    @Override
    public void setUploadEventHandler(Callback<String> callback) {
        this.uploadHandler = callback;
    }

    @Override
    public void setDownloadEventHandler(Callback<String> callback) {
        this.downloadHandler = callback;
    }

    @Override
    public void setListLocalDirectoryEventHandler(BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback) {
        this.localListHandler = callback;
    }

    @Override
    public void setListRemoteDirectoryEventHandler(BiCallback<TreeItem<FileStructureInfo>, List<FileStructureInfo>> callback) {
        this.remoteListHandler = callback;
    }

    @Override
    public void shutdown() {
        remoteStorage.disconnect();
    }
}
