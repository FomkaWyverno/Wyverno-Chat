package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.exceptions.FolderCalculationException;
import ua.wyverno.util.dropbox.hasher.DropboxContentHasher;
import ua.wyverno.util.dropbox.hasher.HexUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

public class FileComputeHash implements ComputeHash, Hashing, ConvertorFileHash {
    private static final Logger logger = LoggerFactory.getLogger(FileComputeHash.class);
    private String hash;
    private final Path originFile;
    private final Path relativePath;

    public FileComputeHash(Path originFile, Path relativePath) {
        this.originFile = originFile;
        this.relativePath = relativePath;
    }

    @Override
    public void computeHash() throws IOException {
        if (this.getOriginFile().toFile().isDirectory()) {
            logger.warn("Try hashing file which is directory! Path: {}", this.getOriginFile());
            throw new FolderCalculationException("Try hashing file which is directory! Path: " + this.getOriginFile());
        }

        logger.debug("Hashing file for method DropBox SHA256 - {}", this.getOriginFile());
        MessageDigest hasher = new DropboxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(this.getOriginFile().toFile())) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        }
        this.hash = HexUtils.hex(hasher.digest());
        logger.debug("Calculate SHA256 by Method Dropbox API for file {} | Content hash = {}", this.getOriginFile(), this.hash);
    }

    public Path getOriginFile() {
        return originFile;
    }

    public Path getRelativePath() {
        return relativePath;
    }

    @Override
    public String getHash() {
        return this.hash;
    }

    @Override
    public FileHash mergeWithOtherFileHash(FileHash root) {
        FileHash fileHash = this.toFileHash();
        return mergeWithOtherFile(fileHash, root);
    }

    private FileHash mergeWithOtherFile(FileHash fileHash, FileHash root) {
        for (FileHash file : root.getChildren()) {

        }
    }

    @Override
    public FileHash toFileHash() {
        return this.convertToFileHash(this.getRelativePath(), this.getOriginFile().toFile().isFile());
    }

    private FileHash convertToFileHash(Path path, boolean headIsFile) {
        Path parent = path.getParent();
        FileHash fileHash;
        if (parent != null) {
            FileHash parentFile = this.convertToFileHash(parent, false);
            fileHash = new FileHash(parentFile, path.toFile().getName(), headIsFile);
            parentFile.addChild(fileHash);
        } else {
            fileHash = new FileHash(path.toFile().getName(), headIsFile);
        }
        return fileHash;
    }
}
