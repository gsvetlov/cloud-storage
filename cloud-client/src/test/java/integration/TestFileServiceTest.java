package integration;

import ru.svetlov.domain.file.FileStructureInfo;
import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.impl.LocalFileService;

public class TestFileServiceTest {
    private static FileViewService service;
    public static void main(String[] args) {
        service = new LocalFileService();
        service.getListView("c:/java").forEach(TestFileServiceTest::displayViewObject);
    }

    private static void displayViewObject(FileStructureInfo o){
        StringBuilder sb = new StringBuilder();
        sb.append(o.getParent()).append("|")
                .append(o.getFilename()).append("|")
                .append(o.getLastModified()).append("|")
                .append(o.getSize()).append("|");
        System.out.println(sb.toString());
    }
}
