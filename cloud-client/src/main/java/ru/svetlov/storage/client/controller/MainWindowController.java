package ru.svetlov.storage.client.controller;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.storage.client.controller.util.TreeViewBuilder;
import ru.svetlov.storage.client.controller.util.TreeViewHandler;
import ru.svetlov.storage.client.controller.util.UserAlert;
import ru.svetlov.storage.client.factory.Factory;
import ru.svetlov.domain.service.viewer.domain.FileType;
import ru.svetlov.storage.client.service.adapter.CloudClientService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Initializable {

    private final ExecutorService executors = Executors.newFixedThreadPool(2);
    private CloudClientService client;
    private boolean hasLogin = false;
    private boolean remoteInProgress;
    private boolean isDirectory;
    private TreeViewHandler localTree;
    private TreeViewHandler remoteTree;

    @FXML
    Button loginButton, uploadButton, downloadButton;
    @FXML
    TreeView<FileStructureInfo> localFileView, remoteFileView;
    @FXML
    TextField host, port, username, password;
    @FXML
    HBox loginBox, controlBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = Factory.getCloudClient();
        client.setLoginEventHandler(this::processLogin);
        client.setUploadEventHandler(this::processTransfer);
        client.setDownloadEventHandler(this::processTransfer);
        setView();
        initLocalView();
    }

    public void shutdown(WindowEvent windowEvent) {
        client.shutdown();
        executors.shutdown();
    }

    private void processLogin(String msg) {
        Platform.runLater(() -> {
            hasLogin = msg.equals("login_successful");
            if (hasLogin) {
                initRemoteView();
            } else {
                alertUser(msg);
            }
            setView();
        });
    }

    private void alertUser(String msg) {
        Platform.runLater(() -> {
            UserAlert.get(msg, "Login error").showAndWait();
        });

    }

    private void setView() {
        loginBox.setDisable(hasLogin);
        loginBox.setManaged(!hasLogin);
        loginBox.setVisible(!hasLogin);
        controlBox.setVisible(hasLogin);
        controlBox.setManaged(hasLogin);
        controlBox.setDisable(remoteInProgress || isDirectory);
    }

    private void initLocalView() {
        localTree = TreeViewBuilder.build(localFileView,
                "C:\\",
                this::localViewSelectionChanged,
                client::listLocal);
        client.setListLocalDirectoryEventHandler(localTree::addTreeItem);
        executors.execute(() -> localTree.update(localFileView.getRoot()));
    }

    private void localViewSelectionChanged(Observable observable,
                                           TreeItem<FileStructureInfo> oldValue,
                                           TreeItem<FileStructureInfo> newValue) {
        isDirectory = newValue.getValue().getType() == FileType.DIRECTORY;
        setView();
        executors.execute(() -> localTree.update(newValue));
    }

    private void initRemoteView() {
        remoteTree = TreeViewBuilder.build(remoteFileView,
                ".",
                this::remoteViewSelectionChanged,
                client::listRemote);
        client.setListRemoteDirectoryEventHandler(remoteTree::addTreeItem);
        executors.execute(() -> remoteTree.update(remoteFileView.getRoot()));
    }

    private void remoteViewSelectionChanged(Observable observable,
                                            TreeItem<FileStructureInfo> oldValue,
                                            TreeItem<FileStructureInfo> newValue) {
        isDirectory = newValue.getValue().getType() == FileType.DIRECTORY;
        setView();
        executors.execute(() -> remoteTree.update(newValue));
    }


    public void btLoginClicked() {
        if (host.getText().isEmpty()
                || port.getText().isEmpty()
                || username.getText().isEmpty()
                || password.getText().isEmpty())
            return;
        executors.execute(() ->
                client.login(host.getText(), port.getText(), username.getText(), password.getText()));
    }

    public void btDownloadClicked() {

    }

    public void btUploadClicked() {
        FileStructureInfo file = localFileView.getSelectionModel().getSelectedItem().getValue();
        remoteInProgress = true;
        setView();
        executors.execute(() -> client.uploadFile(file));
    }

    private void processTransfer(String msg) {
        Platform.runLater(() -> {
            remoteInProgress = false;
            setView();
        });
    }
}
