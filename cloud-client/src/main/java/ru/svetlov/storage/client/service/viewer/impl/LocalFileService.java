package ru.svetlov.storage.client.service.viewer.impl;

import ru.svetlov.domain.service.viewer.FileInfoProvider;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LocalFileService implements FileInfoProvider {
    private static final int PATH_DEPTH = 1;
    private final List<FileStructureInfo> files = new ArrayList<>();

    @Override
    public List<FileStructureInfo> getList(String path, String rootPath) {
        files.clear();
        getPathObjects(Paths.get(path));
        return files;
    }

    private void getPathObjects(Path path) {
        try {
            Files.walkFileTree(
                    path, EnumSet.noneOf(FileVisitOption.class), PATH_DEPTH,
                    new LocalFileVisitor(files::add));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
