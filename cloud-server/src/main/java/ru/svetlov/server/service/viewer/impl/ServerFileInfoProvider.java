package ru.svetlov.server.service.viewer.impl;

import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.domain.service.viewer.FileInfoProvider;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ServerFileInfoProvider implements FileInfoProvider {
    private static final int PATH_DEPTH = 1;
    private final List<FileStructureInfo> files = new ArrayList<>();

    @Override
    public List<FileStructureInfo> getList(String path, String rootPath) {
        files.clear();
        getPathObjects(Paths.get(rootPath, path).normalize(), Paths.get(rootPath));
        return files;
    }

    private void getPathObjects(Path path, Path rootPath) {
        try {
            Files.walkFileTree(
                    path, EnumSet.noneOf(FileVisitOption.class), PATH_DEPTH,
                    new ServerFileVisitor(rootPath, files::add));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
