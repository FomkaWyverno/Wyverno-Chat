package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;

public class HashSumFiles {

    private static final Logger logger = LoggerFactory.getLogger(HashSumFiles.class);

    private final HashMap<Path, Long> hashs = new HashMap<>();

    private final Path root;
    private final List<Path> files;
    public HashSumFiles(Path root, List<Path> files) {
        this.root = root;
        this.files = files;
    }

    public void toHashFiles() throws IOException {
        for (Path file : this.files) {
            logger.debug("Hashing file - {}", file);
            byte[] bytesFile = Files.readAllBytes(file);
            logger.trace("Read bytes file!");


            CRC32 crc32 = new CRC32();
            crc32.update(bytesFile);
            logger.debug("Calculate CRC32 for file = 0x{}", Long.toHexString(crc32.getValue()));

            Path relativePath = this.root.relativize(file);

            this.hashs.put(relativePath, crc32.getValue());
        }
    }

    public HashMap<Path, Long> getHashs() {
        return this.hashs;
    }
}
