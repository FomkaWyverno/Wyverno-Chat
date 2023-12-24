package ua.wyverno;

import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.dropbox.files.upload.UploadFile;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.cloud.SyncCloudStorage;
import ua.wyverno.files.hashs.FileHashInfo;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Path pathCloudFiles = Paths.get("./cloud-files.json");

    public static void main(String[] args) {
        try {

            Config config = loadConfig();//loading Configuration Application
            if (config == null) return;

            DropBoxAPI dropBoxAPI = connectToDropBoxAPI(config);//Connect to API

//            FileCollectorVisitor fileCollectorVisitor = collectFilesApplication(config);
//
//            HashSumFiles sumFiles = new HashSumFiles(config.getPathApplication(), fileCollectorVisitor.getFilesPath());
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            List<FileHashInfo> filesApplication = sumFiles.getRelativizeRootFilesHashInfo();
//            List<FileHashInfo> cloudFiles = mapper.readValue(pathCloudFiles.toFile(), new TypeReference<>() {});
//
//            SyncCloudStorage syncCloudStorage = new SyncCloudStorage(filesApplication, cloudFiles);
//
//            syncCloudStorage.synchronizedWithCloudStorage(null, pathCloudFiles);

            //dropBoxAPI.createFolders(List.of("/folder/test1", "/folder1/test2", "/folder2/test4"));

            List<CloudLocalFile> list = new ArrayList<>(List.of(
                    new CloudLocalFile(
                            Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\src\\main\\java\\ua\\wyverno\\Main.java"),
                            Paths.get("\\Main.java")),
                    new CloudLocalFile(
                            Paths.get("D:\\MyProgram\\ElectronJS-Program\\Overlay\\java\\DeployAppTool\\src\\main\\java\\ua\\wyverno\\util\\http\\ParserParameters.java"),
                            Paths.get("\\ParserParameters.java"))));

            dropBoxAPI.uploadFiles(list);
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    private static FileCollectorVisitor collectFilesApplication(Config config) throws IOException {
        logger.info("Start collect files!");
        FileCollectorVisitor fileCollectorVisitor = new FileCollectorVisitor();
        Files.walkFileTree(config.getPathApplication(), fileCollectorVisitor);
        logger.info("End collect files!");
        return fileCollectorVisitor;
    }


    private static Config loadConfig() throws IOException {
        Path pathConfig = Paths.get("./setting.properties");
        Config config;
        try {
            config = new Config(pathConfig);
        } catch (FileNotFoundException e) {
            logger.info("Config-file not found! Empty config file created!");
            Config.createDefaultPropertiesFile(pathConfig);
            return null;
        }
        return config;
    }

    private static DropBoxAPI connectToDropBoxAPI(Config config) throws DbxException, IOException {
        String accessTokenDbX = config.getAccessTokenDropBox();
        if (Objects.isNull(accessTokenDbX) || !new DropBoxAPI(accessTokenDbX).isValidAccessToken()) {
            logger.debug("Access Token is not valid or not present! Updating...");
            config.updateDbxAccessToken();
            accessTokenDbX = config.getAccessTokenDropBox();
        } else {
            logger.debug("Access Token is valid!");
        }

        return new DropBoxAPI(accessTokenDbX);
    }

    private static HashSumFiles loadCloudFilesHashSum(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(path.toFile(), HashSumFiles.class);
    }
}
