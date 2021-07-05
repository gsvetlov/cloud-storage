package ru.svetlov.server.service.file.impl;

import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.domain.file.FileType;
import ru.svetlov.server.service.file.FileInfoProvider;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RemoteFileInfoProvider implements FileInfoProvider {
    private final List<FileStructureInfo> files = new ArrayList<>();

    @Override
    public List<FileStructureInfo> getPath(String path, String rootPath) {
        files.clear();
        getPathObjects(Paths.get(rootPath, path).normalize(), Paths.get(rootPath));
        return files;
    }

    private void getPathObjects(Path path, Path rootPath) {
        try {
            Files.walkFileTree(
                    path, EnumSet.noneOf(FileVisitOption.class), 1,
                    new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            Path parent = dir.getRoot() == null ? null : rootPath.relativize(dir.getParent());
                            Path filename = dir.getFileName();
                            register(parent, filename, FileType.DIRECTORY, attrs);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            Path parent = rootPath.relativize(file.getParent());
                            register(parent, file.getFileName(), getFileType(attrs), attrs);
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
        files.add(new FileStructureInfo(
                (parent == null) ? "" : parent.toString(),
                (filename == null) ? "" : filename.toString(),
                type,
                attr.size(),
                attr.lastModifiedTime().toString()));
    }
}