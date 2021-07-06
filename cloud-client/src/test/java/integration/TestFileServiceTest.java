package integration;

import ru.svetlov.domain.service.viewer.FileInfoProvider;
import ru.svetlov.domain.service.viewer.domain.FileStructureInfo;
import ru.svetlov.storage.client.service.viewer.impl.LocalFileService;

public class TestFileServiceTest {
    private static FileInfoProvider service;
    public static void main(String[] args) {
        service = new LocalFileService();
        service.getList("c:/java", null).forEach(TestFileServiceTest::displayViewObject);
    }

    private static void displayViewObject(FileStructureInfo o){
        StringBuilder sb = new StringBuilder();
        sb.append(o.getParent()).append("|")
                .append(o.getFilename()).append("|")
                .append(o.getLastModified()).append("|")
                .append(o.getSize()).append("|");
        System.out.println(sb);
    }
}
