package ru.svetlov.server.service.file;

import ru.svetlov.domain.file.FileStructureInfo;

import java.util.List;

public interface FileInfoProvider {
    List<FileStructureInfo> getPath(String... path);
}
