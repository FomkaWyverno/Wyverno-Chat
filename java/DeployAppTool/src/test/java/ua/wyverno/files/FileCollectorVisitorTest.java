package ua.wyverno.files;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.hashs.FileHashNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileCollectorVisitorTest {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorVisitorTest.class);
    @Test
    void getTreeFiles() {
        try {
            Path root = Paths.get("D:\\MyProgram\\Overlay\\java\\DeployAppTool\\test-folder");
            logger.info("Start visitor root: {}", root);
            FileCollectorVisitor visitor = new FileCollectorVisitor(root);
            Files.walkFileTree(root, visitor);
            logger.info("End visitor");
            FileHashNode file = visitor.getTreeFileNode();
        } catch (IOException e) {
            logger.error("Error: ->",e);
        }
    }
}