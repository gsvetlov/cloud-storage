package ru.svetlov.storage.client.service.file;

import ru.svetlov.storage.client.service.file.dto.FileViewObject;

import java.nio.file.Path;
import java.util.List;

public interface FileViewService {
    List<FileViewObject> getListView(Path path);
    List<FileViewObject> getListView(String path);
}
