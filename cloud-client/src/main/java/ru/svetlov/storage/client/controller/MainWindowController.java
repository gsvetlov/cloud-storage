package ru.svetlov.storage.client.controller;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.factory.Factory;
import ru.svetlov.domain.file.FileType;
import ru.svetlov.storage.client.service.adapter.CloudClient;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Initializable {

    private final ExecutorService executors = Executors.newFixedThreadPool(2);
    private CloudClient client;
    private boolean hasLogin = false;
    private boolean remoteInProgress;
    private boolean isDirectory;

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
        client.setListLocalDirectoryEventHandler(this::addTreeItem);
        client.setListRemoteDirectoryEventHandler(this::addTreeItem);
        setView();
        initLocalView(localFileView);
    }

      public void shutdown(WindowEvent windowEvent) {
        client.shutdown();
        executors.shutdown();
    }

    private void processLogin(String msg) {
        Platform.runLater(()->{
            hasLogin = msg.equals("Login successful");
            if (!hasLogin)
                alertUser(msg);
            else
                initRemoteView(remoteFileView);
            setView();
        });
    }

    private void alertUser(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(msg);
            alert.setTitle("Login error");
            alert.setHeaderText(null);
            alert.showAndWait();
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


    private void initLocalView(TreeView<FileStructureInfo> treeView) {
        treeView.setCellFactory(param -> new FileViewTreeCell());
        treeView.getSelectionModel().selectedItemProperty().addListener(this::localViewSelectionChanged);
        TreeItem<FileStructureInfo> root = new TreeItem<>(
                new FileStructureInfo(
                        null,
                        "C:\\",
                        FileType.DIRECTORY,
                        0,
                        ""));
        executors.execute(() -> client.listLocalDirectory(root));
        treeView.setRoot(root);
    }


    public static class FileViewTreeCell extends TreeCell<FileStructureInfo> {
        @Override
        protected void updateItem(FileStructureInfo item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.getFilename());
        }
    }

    private void localViewSelectionChanged(Observable observable,
                                           TreeItem<FileStructureInfo> oldValue,
                                           TreeItem<FileStructureInfo> newValue) {
        isDirectory = newValue.getValue().getType() == FileType.DIRECTORY;
        setView();
        executors.execute(() -> client.listLocalDirectory(newValue));
    }

    private void initRemoteView(TreeView<FileStructureInfo> treeView) {
        treeView.setCellFactory(param -> new FileViewTreeCell());
        treeView.getSelectionModel().selectedItemProperty().addListener(this::remoteViewSelectionChanged);
        TreeItem<FileStructureInfo> root = new TreeItem<>(
                new FileStructureInfo(
                        null,
                        ".",
                        FileType.DIRECTORY,
                        0,
                        ""));
        executors.execute(() -> client.listRemoteDirectory(root));
        treeView.setRoot(root);
    }
    private void remoteViewSelectionChanged(Observable observable,
                                           TreeItem<FileStructureInfo> oldValue,
                                           TreeItem<FileStructureInfo> newValue) {
        isDirectory = newValue.getValue().getType() == FileType.DIRECTORY;
        setView();
        executors.execute(() -> client.listRemoteDirectory(newValue));
    }

    private void addTreeItem(TreeItem<FileStructureInfo> item, List<FileStructureInfo> listView) {
        for (TreeItem<FileStructureInfo> child : item.getChildren())
            listView.remove(child.getValue());
        Platform.runLater(() -> {
            for (int i = 1; i < listView.size(); i++)
                item.getChildren().add(new TreeItem<>(listView.get(i)));
            item.setExpanded(true);
        });
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
