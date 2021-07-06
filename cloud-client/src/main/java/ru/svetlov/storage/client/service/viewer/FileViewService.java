package ru.svetlov.storage.client.service.viewer;

import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;

import java.nio.file.Path;
import java.util.List;

public interface FileViewService {
    List<FileStructureInfo> getListView(Path path);
    List<FileStructureInfo> getListView(String path);
}
