package ua.wyverno.files.differ;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.FileCollectorVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageDifferTest {

    private static final Logger logger = LoggerFactory.getLogger(StorageDifferTest.class);

    private static StorageDiffer storageDiffer;


    @BeforeAll
    static void initTest() throws IOException {
        Path folder1Root = Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder");
        Path folder2Root = Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder2");

        FileCollectorVisitor visitorFolder1 = new FileCollectorVisitor(folder1Root);
        FileCollectorVisitor visitorFolder2 = new FileCollectorVisitor(folder2Root);

        Files.walkFileTree(folder1Root,visitorFolder1);
        Files.walkFileTree(folder2Root,visitorFolder2);

        storageDiffer = new StorageDiffer(visitorFolder1.getTreeFileNode(), visitorFolder2.getTreeFileNode());
    }

    @Test
    void getAddedFiles() {
        assertEquals(0, storageDiffer.getAddedFiles().size());
    }

    @Test
    void getModifyFiles() {
        assertEquals(0, storageDiffer.getModifyFiles().size());
    }

    @Test
    void getDeletedFiles() {
        assertEquals(0, storageDiffer.getDeletedFiles().size());
    }

    @Test
    void getAddedFolders() {
        assertEquals(0, storageDiffer.getAddedFolders().size());
    }

    @Test
    void getDeletedFolders() {
        assertEquals(2, storageDiffer.getDeletedFolders().size());
        storageDiffer.getDeletedFolders().forEach(item -> logger.info(item.toString()));
    }
}