package ua.wyverno.files.hashs;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileComputeHashTest {

    @Test
    void mergeWithOtherFileHash() {
        Path originFile = Paths.get("src/main/java/example");
        FileHash src = new FileHash("src", false);
        FileHash main = new FileHash(src,"main", false);
        FileHash java = new FileHash(main,"java", false);
        src.addChild(main);
        main.addChild(java);

        assertEquals("src/main/java", java.getPath());

        FileComputeHash fileComputeHash = new FileComputeHash(originFile, originFile);
        FileHash convertFileHash = fileComputeHash.mergeWithOtherFileHash(java);

        assertEquals("src/main/java/src/main/java", convertFileHash.getPath());
    }

    @Test
    void toFileHash() {
        Path originFile = Paths.get("src/main/java/example");
        FileComputeHash fileComputeHash = new FileComputeHash(originFile, originFile);
        FileHash convertFileHash = fileComputeHash.toFileHash();

        FileHash src = new FileHash("src", false);
        FileHash main = new FileHash(src,"main", false);
        FileHash java = new FileHash(main,"java", false);
        FileHash example = new FileHash(java,"example",false);

        src.addChild(main);
        main.addChild(java);
        java.addChild(example);

        assertEquals(convertFileHash, example);
        assertFalse(convertFileHash.isFile());
        assertTrue(convertFileHash.isDirectory());
    }
}