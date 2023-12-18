package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.json.jackson.deserializer.FileHashInfoDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashInfoSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

@JsonSerialize(using = FileHashInfoSerializer.class)
@JsonDeserialize(using = FileHashInfoDeserializer.class)
public class FileHashInfo {

    private static final Logger logger = LoggerFactory.getLogger(FileHashInfo.class);

    private final Path pathFile;
    private long hash;

    public FileHashInfo(Path pathFile) {
        this.pathFile = pathFile;
    }

    public FileHashInfo(Path pathFile, long hash) {
        this.pathFile = pathFile;
        this.hash = hash;
    }

    protected void calculateChecksum() throws IOException {
        logger.debug("Hashing file - {}", this.pathFile);
        byte[] bytesFile = Files.readAllBytes(this.pathFile);
        logger.trace("Read bytes file!");


        CRC32 crc32 = new CRC32();
        crc32.update(bytesFile);
        logger.debug("Calculate CRC32 for file = 0x{}", Long.toHexString(crc32.getValue()));

        this.hash = crc32.getValue();
    }

    public Path getPathFile() {
        return pathFile;
    }

    public long getHash() {
        return hash;
    }
}
