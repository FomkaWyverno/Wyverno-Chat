package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.IFile;
import ua.wyverno.files.exceptions.FolderCalculationException;
import ua.wyverno.json.jackson.deserializer.FileHashDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashSerializer;
import ua.wyverno.util.dropbox.hasher.DropboxContentHasher;
import ua.wyverno.util.dropbox.hasher.HexUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;

/**
 * Class just have Path File and its content hash
 */
@JsonSerialize(using = FileHashSerializer.class)
@JsonDeserialize(using = FileHashDeserializer.class)
public class FileHash implements IFile<FileHash>, Hashing {

    private static final Logger logger = LoggerFactory.getLogger(FileHash.class);
    private String hash;

    private final Path absolutePath;
    private final Path relativePath;

    public FileHash(Path relativePath, Path absolutePath) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
    }

//    public FileHash(Path relativePath, Path absolutePath, FileHash ) {
//
//    }

    public FileHash(Path relativePath, Path absolutePath, String hash) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.hash = hash;
    }

    public FileHash(String relativePath, String absolutePath) {
        this.absolutePath = Paths.get(absolutePath);
        this.relativePath = Paths.get(relativePath);
    }

    public FileHash(String relativePath, String absolutePath, String hash) {
        this.absolutePath = Paths.get(absolutePath);
        this.hash = hash;
        this.relativePath = Paths.get(relativePath);
    }

    @Override
    public void calculateChecksum() throws IOException {
        if (this.isDirectory()) {
            logger.warn("Try hashing file which is directory! Path: {}", this.getAbsolutePath());
            throw new FolderCalculationException("Try hashing file which is directory! Path: " + this.getAbsolutePath());
        }

        logger.debug("Hashing file for method DropBox SHA256 - {}", this.getPath());
        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this.getAbsolutePath().toFile())) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());
        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.getAbsolutePath(), this.hash);

    }

    @Override
    public String getHash() {
        return hash;
    }
    @Override
    public boolean isDirectory() {
        return this.absolutePath.toFile().isDirectory();
    }

    @Override
    public boolean isFile() {
        return this.absolutePath.toFile().isFile();
    }

    @Override
    public Path getPath() {
        return this.relativePath;
    }

    @Override
    public Path getAbsolutePath() {
        return this.absolutePath;
    }

    @Override
    public FileHash getParent() {
        return null;
    }

    @Override
    public List<FileHash> getChildren() {
        return null;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileHash otherFile)) return false;
        return this.hash.equals(otherFile.getHash())
                &&
                this.getPath().equals(otherFile.getPath());
    }

    @Override
    public String toString() {
        return "FileHash{" +
                "pathFile=" + this.getPath() +
                ", relativePath=" + this.relativePath.toString() +
                ", hash='" + hash + '\'' +
                '}';
    }
}
