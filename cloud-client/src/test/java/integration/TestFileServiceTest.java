package integration;

import ru.svetlov.storage.client.service.file.FileViewService;
import ru.svetlov.storage.client.service.file.dto.FileViewObject;
import ru.svetlov.storage.client.service.file.impl.TestFileService;

public class TestFileServiceTest {
    private static FileViewService service;
    public static void main(String[] args) {
        service = new TestFileService();
        service.getListView("c:/java").forEach(TestFileServiceTest::displayViewObject);
    }

    private static void displayViewObject(FileViewObject o){
        StringBuilder sb = new StringBuilder();
        sb.append(o.getParent()).append("|")
                .append(o.getFilename()).append("|")
                .append(o.getLastModified()).append("|")
                .append(o.getSize()).append("|");
        System.out.println(sb.toString());
    }
}
