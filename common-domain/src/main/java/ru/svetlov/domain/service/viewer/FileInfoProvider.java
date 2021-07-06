package ru.svetlov.domain.service.viewer;

import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;

import java.util.List;

public interface FileInfoProvider {
    List<FileStructureInfo> getList(String path, String rootPath);
}
