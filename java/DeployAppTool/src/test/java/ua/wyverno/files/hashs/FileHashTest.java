package ua.wyverno.files.hashs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileHashTest {

    private static final Logger logger = LoggerFactory.getLogger(FileHashTest.class);

    @ParameterizedTest
    @MethodSource("sourceIsDirectory")
    void isDirectory(FileHash file, boolean expect) {
        assertEquals(file.isDirectory(), expect);
    }

    @ParameterizedTest
    @MethodSource("sourceIsDirectory")
    void isFile(FileHash file, boolean expect) {
        assertEquals(file.isFile(), !expect);
    }

    public static Stream<Arguments> sourceIsDirectory() {
        FileHash cloudFile = new FileHash(Paths.get("/file/cloud.java"),Paths.get("/file/cloud.java"));
        FileHash cloudFolder = new FileHash(Paths.get("/cloud"),Paths.get("/cloud"));
        cloudFile.setCloudFile(true);
        cloudFolder.setCloudDirectory(true);
        FileHash localFile = new FileHash(Paths.get("/Main.java"),Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder\\Main.java"));
        FileHash localFolder = new FileHash(Paths.get("/dropbox"),Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder\\dropbox"));
        return Stream.of(
                Arguments.of(cloudFile, false),
                Arguments.of(cloudFolder, true),
                Arguments.of(localFile, false),
                Arguments.of(localFolder, true));
    }


    public static void main(String[] args) {
        Path slashRoot = Paths.get("/");
        Path path = Paths.get("/master/full/link.java");
        Path systemPath = Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder\\dropbox");

        logger.info("Resolve: {}", slashRoot.resolve(path));
        logger.info("Relatavize: {}", slashRoot.relativize(path));
        logger.info("StartWith systemPath: {}", systemPath.startsWith(slashRoot));
        logger.info("StartWith path: {}", path.startsWith(slashRoot));
    }
}