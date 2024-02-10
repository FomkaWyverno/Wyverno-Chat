package ua.wyverno.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.hashs.FileMetadataNode;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collects file and folder paths
 */
public class FileCollectorVisitor implements FileVisitor<Path> {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorVisitor.class);

    private final List<Path> filesPath = new ArrayList<>();
    private final List<Path> folderPath = new ArrayList<>();
    protected final Path root;
    private FileMetadataNode treeFileNode;
    public FileCollectorVisitor(Path root) {
        this.root = Objects.requireNonNull(root);
    }

    public List<Path> getFilesPath() {
        return filesPath;
    }
    public List<Path> getFolderPath() {
        return folderPath;
    }

    public FileMetadataNode getTreeFileNode() {
        return this.treeFileNode;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        logger.trace("Pre visit Directory: {}",dir.toString());
        this.folderPath.add(dir);
        Path relativeFolder = this.root.relativize(dir);
        Path emptyPath = Paths.get("");
        if (relativeFolder.equals(emptyPath)) {
            this.treeFileNode = new FileMetadataNode(".",false);
        } else {
            FileMetadataNode fileHash = new FileMetadataNode(this.treeFileNode, relativeFolder.toFile().getName(), false);
            this.treeFileNode.addChild(fileHash);
            this.treeFileNode = fileHash;
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        logger.debug("Visit File: {}", file.toString());
        this.filesPath.add(file);
        Path relativeFile = this.root.relativize(file);
        FileMetadataNode fileHash = new FileMetadataNode(this.treeFileNode, relativeFile.toFile().getName(), true);
        this.treeFileNode.addChild(fileHash);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        logger.error("Visit File Failed: {}",file.toString(), exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        logger.trace("Post visit Directory: {}",dir.toString());
        if (this.treeFileNode.getParent() != null) this.treeFileNode = this.treeFileNode.getParent();
        return FileVisitResult.CONTINUE;
    }
}
