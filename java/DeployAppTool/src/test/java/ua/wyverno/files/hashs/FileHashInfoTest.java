package ua.wyverno.files.hashs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileHashInfoTest {


    @ParameterizedTest
    @MethodSource("sourceIsDirectory")
    void isDirectory(FileHashInfo file, boolean expect) {
        assertEquals(file.isDirectory(), expect);
    }

    @ParameterizedTest
    @MethodSource("sourceIsDirectory")
    void isFile(FileHashInfo file, boolean expect) {
        assertEquals(file.isFile(), !expect);
    }

    public static Stream<Arguments> sourceIsDirectory() {
        FileHashInfo cloudFile = new FileHashInfo(Paths.get("/file/cloud.java"));
        FileHashInfo cloudFolder = new FileHashInfo(Paths.get("/cloud"));
        cloudFile.setCloudFile(true);
        cloudFolder.setCloudDirectory(true);
        FileHashInfo localFile = new FileHashInfo(Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder\\Main.java"));
        FileHashInfo localFolder = new FileHashInfo(Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\test-folder\\dropbox"));
        return Stream.of(
                Arguments.of(cloudFile, false),
                Arguments.of(cloudFolder, true),
                Arguments.of(localFile, false),
                Arguments.of(localFolder, true));
    }
}