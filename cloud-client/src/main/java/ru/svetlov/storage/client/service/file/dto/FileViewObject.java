package ru.svetlov.storage.client.service.file.dto;

import ru.svetlov.storage.client.service.file.enums.FileType;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.List;

/** Класс для передачи объектов файловой системы.
 *  <p>{@code parent} - элемент родитель
 *  <p>{@code filename} - имя файла / директории
 *  <p>{@code type} - тип объекта {@link ru.svetlov.storage.client.service.file.enums.FileType}
 *  <p>для директорий возможно задать список потомков {@link #children children}
 */
public class FileViewObject {
    private final String parent;
    private final String filename;
    private final FileType type;
    private List<FileViewObject> children;

    private final long size;
    private final FileTime lastModified;

    public FileViewObject(String parent, String filename, FileType type, long size, FileTime lastModified) {
        this.parent = parent;
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getParent() {
        return parent;
    }

    public String getFilename() {
        return filename;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public FileTime getLastModified() {
        return lastModified;
    }

    public List<FileViewObject> getChildren() {
        return children;
    }

    public void setChildren(List<FileViewObject> descendants) {
        if (type != FileType.DIRECTORY) throw new IllegalArgumentException("only directories can have descendants");
        this.children = descendants;
    }
}
