package ru.svetlov.storage.client.controller;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.svetlov.storage.client.factory.Factory;
import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.dto.FileViewObject;
import ru.svetlov.storage.client.service.file.enums.FileType;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.SQLOutput;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private FileViewService fileService;

    @FXML
    TreeView<FileViewObject> leftTreeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileService = Factory.getService();
        initTreeView(leftTreeView);
    }


    public void shutdown() {

    }

    private void initTreeView(TreeView<FileViewObject> treeView) {
        treeView.setCellFactory(param -> new FileViewTreeCell());
        treeView.getSelectionModel().selectedItemProperty().addListener(this::leftTreeSelectionChanged);
        TreeItem<FileViewObject> root = new TreeItem<>(
                new FileViewObject(
                        null,
                        "C:",
                        FileType.DIRECTORY,
                        0,
                        FileTime.fromMillis(System.currentTimeMillis())));
        addTreeItem(root, Paths.get("c:/"));
        treeView.setRoot(root);
    }

    private void addTreeItem(TreeItem<FileViewObject> item, Path path) {
        List<FileViewObject> listView = fileService.getListView(path);
        for (int i = 1; i < listView.size(); i++) {
            item.getChildren().add(new TreeItem<>(listView.get(i)));
        }
        item.setExpanded(true);
    }

    public static class FileViewTreeCell extends TreeCell<FileViewObject> {
        @Override
        protected void updateItem(FileViewObject item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.getFilename());
        }
    }

    private void leftTreeSelectionChanged(Observable observable, TreeItem<FileViewObject> oldValue, TreeItem<FileViewObject> newValue) {
        if (newValue.getParent() == null) return;
        FileType type = newValue.getValue().getType();
        String parent = newValue.getValue().getParent();
        String filename = newValue.getValue().getFilename();
        Path path = Paths.get(parent, filename);
        if (type == FileType.DIRECTORY)
            addTreeItem(newValue, path);
    }

}
