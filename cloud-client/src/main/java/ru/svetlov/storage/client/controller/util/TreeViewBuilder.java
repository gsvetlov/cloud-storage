package ru.svetlov.storage.client.controller.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.domain.file.FileType;

import java.util.function.Consumer;

public class TreeViewBuilder {

    public static TreeViewHandler build(TreeView<FileStructureInfo> treeView,
                                        String filename,
                                        ChangeListener<TreeItem<FileStructureInfo>> listener,
                                        Consumer<TreeItem<FileStructureInfo>> updater) {

        treeView.setCellFactory(param -> new FileViewTreeCell());
        treeView.getSelectionModel().selectedItemProperty().addListener(listener);

        FileStructureInfo rootPath = new FileStructureInfo(null, filename, FileType.DIRECTORY, 0l, "");
        treeView.setRoot(new TreeItem<>(rootPath));

        return new TreeViewHandler(treeView, updater);
    }

    public static class FileViewTreeCell extends TreeCell<FileStructureInfo> {
        @Override
        protected void updateItem(FileStructureInfo item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.getFilename());
        }
    }
}
