package ru.svetlov.domain.service.viewer;

import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.domain.service.viewer.domain.FileType;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

public abstract class BaseFileVisitor implements FileVisitor<Path> {

    protected final Function<FileStructureInfo, Boolean> function;

    protected BaseFileVisitor(Function<FileStructureInfo, Boolean> function) {
        this.function = function;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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

    protected FileType getFileType(BasicFileAttributes attrs) {
        if (attrs.isDirectory()) return FileType.DIRECTORY;
        if (attrs.isRegularFile()) return FileType.FILE;
        if (attrs.isSymbolicLink()) return FileType.SYMLINK;
        return FileType.OTHER;
    }

    protected void register(Path parent, Path filename, FileType type, BasicFileAttributes attr) {
        function.apply(new FileStructureInfo(
                (parent == null) ? "" : parent.toString(),
                (filename == null) ? "" : filename.toString(),
                type,
                attr.size(),
                attr.lastModifiedTime().toString()));
    }
}
