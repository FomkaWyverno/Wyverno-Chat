package ua.wyverno.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileCollectorVisitor implements FileVisitor<Path> {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorVisitor.class);

    private final List<Path> filesPath = new ArrayList<>();
    private final List<Path> folderPath = new ArrayList<>();

    public List<Path> getFilesPath() {
        return filesPath;
    }

    public List<Path> getFolderPath() {
        return folderPath;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        logger.trace("Pre visit Directory: {}",dir.toString());
        this.folderPath.add(dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        logger.debug("Visit File: {}", file.toString());
        this.filesPath.add(file);
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
        return FileVisitResult.CONTINUE;
    }
}
