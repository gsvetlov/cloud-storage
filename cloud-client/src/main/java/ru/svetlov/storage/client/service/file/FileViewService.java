package ru.svetlov.storage.client.service.file;

import ru.svetlov.domain.file.FileStructureInfo;

import java.nio.file.Path;
import java.util.List;

public interface FileViewService {
    List<FileStructureInfo> getListView(Path path);
    List<FileStructureInfo> getListView(String path);
}
