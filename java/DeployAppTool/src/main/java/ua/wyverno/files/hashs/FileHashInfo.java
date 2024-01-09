package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.json.jackson.deserializer.FileHashInfoDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashInfoSerializer;
import ua.wyverno.util.dropbox.hasher.DropboxContentHasher;
import ua.wyverno.util.dropbox.hasher.HexUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void calculateChecksum() throws IOException {
        logger.debug("Hashing file for method DropBox SHA256 - {}", this.pathFile);

        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this.pathFile.toFile())) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());

        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.pathFile, this.hash);
    }

    public Path getPathFile() {
        return pathFile;
    }

    public String getHash() {
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
