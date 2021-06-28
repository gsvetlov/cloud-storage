package ru.svetlov.storage.client.factory;

import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.impl.TestFileService;

public class Factory {
    public static FileViewService getService() {
        return new TestFileService();
    }
}
