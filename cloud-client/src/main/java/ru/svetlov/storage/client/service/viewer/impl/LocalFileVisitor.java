package ru.svetlov.storage.client.service.viewer.impl;

import ru.svetlov.domain.service.viewer.BaseFileVisitor;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.domain.service.viewer.domain.FileType;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

public class LocalFileVisitor extends BaseFileVisitor {

    public LocalFileVisitor(Function<FileStructureInfo, Boolean> function) {
        super(function);
    }

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
}
