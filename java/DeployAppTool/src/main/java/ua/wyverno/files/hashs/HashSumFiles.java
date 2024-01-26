package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate content hash for list files
 */
public class HashSumFiles {

    private static final Logger logger = LoggerFactory.getLogger(HashSumFiles.class);

    private final Path root;
    private List<Path> originalFiles;
    private List<FileHashInfo> filesHashInfo;
    private List<FileHashInfo> relativizeRootFilesHashInfo;

    public HashSumFiles(Path root, List<Path> files) {
        this.root = root;
        this.originalFiles = files;
    }

    /**
     * Calculate for files content hash
     * @throws IOException generating when has problem reading file
     */
    private void calculateFilesHashInfo() throws IOException {
        logger.debug("Start calculate for files hash info");
        List<FileHashInfo> calculatedFilesHash = new ArrayList<>();

        for (Path p : this.originalFiles) {
            FileHashInfo fileHashInfo = new FileHashInfo(p);
            fileHashInfo.calculateChecksum();
            calculatedFilesHash.add(fileHashInfo);
        }
        this.filesHashInfo = calculatedFilesHash;
    }

    public List<FileHashInfo> getOriginalFiles() {
        return this.filesHashInfo;
    }

    public List<FileHashInfo> getFilesHashInfo() throws IOException {
        if (this.filesHashInfo != null) return filesHashInfo;
        this.calculateFilesHashInfo();
        return this.filesHashInfo;
    }

    public List<FileHashInfo> getRelativizeRootFilesHashInfo() throws IOException {
        if (this.relativizeRootFilesHashInfo != null) return this.relativizeRootFilesHashInfo;

        this.relativizeRootFilesHashInfo = new ArrayList<>();

        for (FileHashInfo fileHashInfo : this.getFilesHashInfo()) {
            Path relativizeRootPath = Paths.get("/")
                    .resolve(this.root
                            .relativize(fileHashInfo.getPathFile()));

            FileHashInfo relativizeFileHashInfo = new FileHashInfo(relativizeRootPath, fileHashInfo.getHash());

            this.relativizeRootFilesHashInfo.add(relativizeFileHashInfo);
        }

        return relativizeRootFilesHashInfo;
    }
}
