package ru.svetlov.domain.file;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс для передачи объектов файловой системы.
 * <p>{@code parent} - элемент родитель
 * <p>{@code filename} - имя файла / директории
 * <p>{@code type} - тип объекта {@link ru.svetlov.domain.file.FileType}
 * <p>{@code size} - размер файла
 * <p>{@code filename} - время последнего изменения
 * <p>для директорий возможно задать список потомков {@link #children children}
 */

@Data
@NoArgsConstructor
public class FileStructureInfo {
    private String parent;
    private String filename;
    private FileType type;
    private List<FileStructureInfo> children;
    private long size;
    private String lastModified;

    public FileStructureInfo(String parent, String filename, FileType type, long size, String lastModified) {
        this.parent = parent;
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified.toString();
    }

    public void setChildren(List<FileStructureInfo> descendants) {
        //if (type != FileType.DIRECTORY) throw new IllegalArgumentException("only directories can have descendants");
        this.children = descendants;
    }
}

