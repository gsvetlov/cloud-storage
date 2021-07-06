package ru.svetlov.storage.client.controller.util;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;

import java.util.List;
import java.util.function.Consumer;

public class TreeViewHandler {
    private final TreeView<FileStructureInfo> tree;
    private final Consumer<TreeItem<FileStructureInfo>> updater;

    public TreeViewHandler(
            TreeView<FileStructureInfo> tree,
            Consumer<TreeItem<FileStructureInfo>> updater) {
        this.tree = tree;
        this.updater = updater;
    }

    public void update(TreeItem<FileStructureInfo> item) {
        updater.accept(item);
    }

    public void addTreeItem(TreeItem<FileStructureInfo> item, List<FileStructureInfo> listView) {
        for (TreeItem<FileStructureInfo> child : item.getChildren())
            listView.remove(child.getValue());
        Platform.runLater(() -> {
            for (int i = 1; i < listView.size(); i++)
                item.getChildren().add(new TreeItem<>(listView.get(i)));
            item.setExpanded(true);
        });
    }
}
