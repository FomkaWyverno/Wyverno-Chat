package ua.wyverno.files.cloud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.FileHashInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SyncCloudStorageTest {

    private static SyncCloudStorage syncCloudStorage;

    @BeforeAll
    public static void setUp() throws IOException {
        FileCollectorVisitor visitor = new FileCollectorVisitor();
        Files.walkFileTree(Paths.get("./test-folder"),visitor);


//        syncCloudStorage = new SyncCloudStorageBuilder()
//                .cloudFiles(cloudFiles)
//                .cloudFolders(cloudFolders)
//                .applicationAbsolutePathFiles(appAbsolutePathFiles)
//                .applicationRelativizedPathFiles(appRelPathFiles)
//                .applicationFoldersRelativized(appRelFolders)
//                .build();
    }

    @Test
    void getDeletedFiles() {
        assertEquals(0, syncCloudStorage.getDeletedFiles().size());
    }

    @Test
    void getAddedOrModifyFiles() {
    }

    @Test
    void getAddedFolders() {
    }

    @Test
    void getDeletedFolders() {
    }

    @Test
    void getApplicationFoldersRelativized() {
    }

    @Test
    void getCloudFolders() {
    }

    @Test
    void getApplicationRelativizedPathFiles() {
    }

    @Test
    void getApplicationFiles() {
    }

    @Test
    void getCloudFiles() {
    }
}