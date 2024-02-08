package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Generate content hash for list files
 */
public class HashSumFiles {

    private static final Logger logger = LoggerFactory.getLogger(HashSumFiles.class);

    private final Path root;
    private final List<Path> originalFiles;
    private List<FileHashNode> filesHash;

    public HashSumFiles(Path root, List<Path> files) {
        this.root = root;
        this.originalFiles = files;
    }

    /**
     * Calculate for files content hash
     * @throws IOException generating when has problem reading file
     */
    private void calculateFilesHashInfo() throws IOException {
//        logger.debug("Start calculate for files hash info");
//        List<FileHash> calculatedFilesHash = new ArrayList<>();
//
//        for (Path p : this.originalFiles) {
//            FileHash fileHash = new FileHash(this.root.relativize(p), p);
//            fileHash.calculateChecksum();
//            calculatedFilesHash.add(fileHash);
//        }
//        this.filesHash = calculatedFilesHash;
    }

    public List<FileHashNode> getOriginalFiles() {
        return this.filesHash;
    }

    public List<FileHashNode> getFilesHash() throws IOException {
        if (this.filesHash != null) return filesHash;
        this.calculateFilesHashInfo();
        return this.filesHash;
    }
}
