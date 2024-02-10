package ua.wyverno.files.hashs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.FileCollectorVisitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileCollectorWithHashVisitor extends FileCollectorVisitor {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorWithHashVisitor.class);

    public FileCollectorWithHashVisitor(Path root) {
        super(root);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        try {
            logger.debug("Visit File: {}", file.toString());

            this.getFilesPath().add(file);
            Path relativeFile = this.root.relativize(file);
            FileComputeHash fileComputeHash = new FileComputeHash(file);
            fileComputeHash.computeHash();
            FileMetadataNode fileHash = new FileMetadataNode(this.getTreeFileNode(), relativeFile.toFile().getName(), true, fileComputeHash.getContentHash());
            this.getTreeFileNode().addChild(fileHash);

            return FileVisitResult.CONTINUE;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
