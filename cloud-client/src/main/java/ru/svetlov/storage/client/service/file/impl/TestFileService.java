package ru.svetlov.storage.client.service.file.impl;

import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.dto.FileViewObject;
import ru.svetlov.storage.client.service.file.enums.FileType;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TestFileService implements FileViewService {
    private final List<FileViewObject> files = new ArrayList<>();

    @Override
    public List<FileViewObject> getListView(Path path) {
        files.clear();
        getPathObjects(path);
        return files;
    }

    @Override
    public List<FileViewObject> getListView(String path) {
        return getListView(Paths.get(path));
    }

    private void getPathObjects(Path path) {
        try {
            Files.walkFileTree(
                    path, EnumSet.noneOf(FileVisitOption.class), 1,
                    new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            Path parent = dir.getRoot() == null ? null : dir.getParent();
                            Path filename = dir.getFileName();
                            register(parent, filename, FileType.DIRECTORY, attrs);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            register(file.getParent(), file.getFileName(), getFileType(attrs), attrs);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileType getFileType(BasicFileAttributes attrs) {
        if (attrs.isDirectory()) return FileType.DIRECTORY;
        if (attrs.isRegularFile()) return FileType.FILE;
        if (attrs.isSymbolicLink()) return FileType.SYMLINK;
        return FileType.OTHER;
    }

    private void register(Path parent, Path filename, FileType type, BasicFileAttributes attr) {
        files.add(new FileViewObject(
                (parent == null) ? "" : parent.toString(),
                (filename == null) ? "" : filename.toString(),
                type,
                attr.size(),
                attr.lastModifiedTime()));
    }
}
