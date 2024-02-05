package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.exceptions.FolderCalculationException;
import ua.wyverno.json.jackson.deserializer.FileHashDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashSerializer;
import ua.wyverno.util.dropbox.hasher.DropboxContentHasher;
import ua.wyverno.util.dropbox.hasher.HexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * Class just have Path File and its content hash
 */
@JsonSerialize(using = FileHashSerializer.class)
@JsonDeserialize(using = FileHashDeserializer.class)
public class FileHash extends File {

    private static final Logger logger = LoggerFactory.getLogger(FileHash.class);
    private String hash;
    private final Path absolutePath;
    private boolean isCloudFile = false;
    private boolean isCloudDirectory = false;

    public FileHash(Path pathFile, Path absolutePath) {
        super(pathFile.toUri());
        this.absolutePath = absolutePath;
    }

    public FileHash(Path pathFile, Path absolutePath, String hash) {
        super(pathFile.toUri());
        this.absolutePath = absolutePath;
        this.hash = hash;
    }

    public FileHash(String pathname, String absolutePath) {
        super(pathname);
        this.absolutePath = Paths.get(absolutePath);
    }

    public FileHash(String pathname, String absolutePath, String hash) {
        super(pathname);
        this.hash = hash;
        this.absolutePath = Paths.get(absolutePath);
    }

    public void calculateChecksum() throws IOException {
        if (this.isDirectory()) {
            logger.warn("Try hashing file which is directory! Path: {}", this.toPath());
            throw new FolderCalculationException("Try hashing file which is directory! Path: " + this.toPath());
        }

        logger.debug("Hashing file for method DropBox SHA256 - {}", this.getPathFile());
        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this)) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());
        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.toPath(), this.hash);

    }

    public Path getPathFile() {
        return super.toPath();
    }

    public String getHash() {
        return hash;
    }

    public void setCloudFile(boolean cloudFile) {
        this.isCloudFile = cloudFile;
        this.isCloudDirectory = !this.isCloudFile;
    }

    public void setCloudDirectory(boolean cloudDirectory) {
        this.isCloudDirectory = cloudDirectory;
        this.isCloudFile = !this.isCloudDirectory;
    }

    @Override
    public boolean isDirectory() {
        if (!this.isCloudFile && this.isCloudDirectory) return true;
        return super.isDirectory();
    }

    @Override
    public boolean isFile() {
        if (this.isCloudFile && !this.isCloudDirectory) return true;
        return super.isFile();
    }

    public boolean isCloud() {
        return this.isCloudFile || this.isCloudDirectory;
    }

    public boolean isCloudFile() {
        return isCloudFile;
    }

    public boolean isCloudDirectory() {
        return isCloudDirectory;
    }

    @Override
    public String getAbsolutePath() {
        return this.absolutePath.toString();
    }

    @Override
    public File getAbsoluteFile() {
        return new File(this.absolutePath.toString());
    }

    public Path toAbsolutePath() {
        return this.absolutePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileHash otherFile)) return false;
        return this.hash.equals(otherFile.getHash())
                &&
                this.getPathFile().equals(otherFile.getPathFile());
    }

    @Override
    public String toString() {
        return "FileHash{" +
                "pathFile=" + this.getPathFile() +
                ", hash='" + hash + '\'' +
                '}';
    }
}
