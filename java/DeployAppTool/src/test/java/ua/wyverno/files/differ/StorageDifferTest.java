package ua.wyverno.files.differ;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.FileCollectorWithHashVisitor;

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
        Path folder1Root = Paths.get(".\\test-folder").toAbsolutePath();
        Path folder2Root = Paths.get(".\\test-folder2").toAbsolutePath();

        FileCollectorVisitor visitorFolder1 = new FileCollectorWithHashVisitor(folder1Root);
        FileCollectorVisitor visitorFolder2 = new FileCollectorWithHashVisitor(folder2Root);

        Files.walkFileTree(folder1Root,visitorFolder1);
        Files.walkFileTree(folder2Root,visitorFolder2);

        storageDiffer = new StorageDiffer(visitorFolder1.getTreeFileNode(), visitorFolder2.getTreeFileNode());
    }

    @Test
    void getAddedFiles() {
        storageDiffer.getAddedFiles().forEach(item -> logger.info("Added file {}", item));
        assertEquals(1, storageDiffer.getAddedFiles().size());
    }
    @Test
    void getAddedFolders() {
        storageDiffer.getAddedFolders().forEach(item -> logger.info("Added Folder {}", item));
        assertEquals(1, storageDiffer.getAddedFolders().size());
    }
    @Test
    void getModifyFiles() {
        storageDiffer.getModifyFiles().forEach(item -> logger.info("Modify File {}", item));
        assertEquals(2, storageDiffer.getModifyFiles().size());
    }

    @Test
    void getDeletedFiles() {
        storageDiffer.getDeletedFiles().forEach(item -> logger.info("Deleted file {}", item));
        assertEquals(3, storageDiffer.getDeletedFiles().size());
    }
    @Test
    void getDeletedFolders() {
        assertEquals(2, storageDiffer.getDeletedFolders().size());
        storageDiffer.getDeletedFolders().forEach(item -> logger.info("Deleted folder {}",item));
    }
}