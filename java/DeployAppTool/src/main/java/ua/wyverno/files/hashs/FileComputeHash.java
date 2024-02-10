package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.exceptions.FolderCalculationException;
import ua.wyverno.util.dropbox.hasher.DropboxContentHasher;
import ua.wyverno.util.dropbox.hasher.HexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

public class FileComputeHash implements ComputeHash, Hashing {
    private static final Logger logger = LoggerFactory.getLogger(FileComputeHash.class);
    private String hash;
    private final Path path;
    private File absoluteFile;

    public FileComputeHash(Path path) {
        this.path = path;
    }

    @Override
    public void computeHash() throws IOException {
        if (this.getPath().toFile().isDirectory()) {
            logger.warn("Try hashing file which is directory! Path: {}", this.getPath());
            throw new FolderCalculationException("Try hashing file which is directory! Path: " + this.getPath());
        }

        logger.debug("Hashing file for method DropBox SHA256 - {}", this.getPath());
        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this.getPath().toFile())) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());
        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.getPath(), this.hash);
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String getContentHash() {
        return this.hash;
    }
}
