package ua.wyverno.files.differ;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.FileHash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageDifferTest {

    private static final Logger logger = LoggerFactory.getLogger(StorageDifferTest.class);

    private static StorageDiffer storageDiffer;


    @BeforeAll
    static void initTest() throws IOException {
        FileCollectorVisitor visitorFolder1 = new FileCollectorVisitor();
        FileCollectorVisitor visitorFolder2 = new FileCollectorVisitor();

        Path folder1Root = Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder");
        Path folder2Root = Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder2");
        Files.walkFileTree(folder1Root,visitorFolder1);
        Files.walkFileTree(folder2Root,visitorFolder2);
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
        assertEquals(1, storageDiffer.getDeletedFolders().size());
    }
}