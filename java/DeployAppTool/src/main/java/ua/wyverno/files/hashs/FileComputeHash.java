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
    private final Path root;
    private final FileHashNode fileHash;
    private File absoluteFile;

    public FileComputeHash(Path root, FileHashNode fileHash) {
        this.root = root;
        this.fileHash = fileHash;
    }

    @Override
    public void computeHash() throws IOException {
        if (this.toAbsoluteFile().isDirectory()) {
            logger.warn("Try hashing file which is directory! Path: {}", this.toAbsoluteFile());
            throw new FolderCalculationException("Try hashing file which is directory! Path: " + this.toAbsoluteFile());
        }

        logger.debug("Hashing file for method DropBox SHA256 - {}", this.toAbsoluteFile());
        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this.toAbsoluteFile())) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());
        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.toAbsoluteFile(), this.hash);
    }

    public File toAbsoluteFile() {
        if (this.absoluteFile == null) this.absoluteFile = this.root.resolve(this.fileHash.getPath()).toFile();
        return this.absoluteFile;
    }

    public Path getRoot() {
        return root;
    }

    public FileHashNode getFileHash() {
        return fileHash;
    }

    @Override
    public String getHash() {
        return this.hash;
    }
}
