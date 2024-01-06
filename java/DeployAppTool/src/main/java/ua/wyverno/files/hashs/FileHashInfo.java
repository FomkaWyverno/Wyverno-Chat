package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.json.jackson.deserializer.FileHashInfoDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashInfoSerializer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

@JsonSerialize(using = FileHashInfoSerializer.class)
@JsonDeserialize(using = FileHashInfoDeserializer.class)
public class FileHashInfo {

    private static final Logger logger = LoggerFactory.getLogger(FileHashInfo.class);

    private final Path pathFile;
    private String hash;

    public FileHashInfo(Path pathFile) {
        this.pathFile = pathFile;
    }

    public FileHashInfo(Path pathFile, String hash) {
        this.pathFile = pathFile;
        this.hash = hash;
    }

    protected void calculateChecksum() throws IOException {
        try {
            logger.debug("Hashing file - {}", this.pathFile);
            byte[] data = Files.readAllBytes(this.pathFile);
            logger.trace("Read bytes file!");
            byte[] hash = MessageDigest.getInstance("SHA256").digest(data);


            this.hash = new BigInteger(1,hash).toString(16);
            logger.debug("Calculate SHA256 for file = 0x{}", this.hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getPathFile() {
        return pathFile;
    }

    public String  getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileHashInfo otherFile)) return false;
        return this.hash.equals(otherFile.getHash())
               &&
               this.pathFile.equals(otherFile.getPathFile());
    }

    @Override
    public String toString() {
        return "FileHashInfo{" +
                "pathFile=" + pathFile +
                ", hash='" + hash + '\'' +
                '}';
    }
}
