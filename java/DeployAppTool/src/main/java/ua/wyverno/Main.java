package ua.wyverno;

import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.SyncCloudStorage;
import ua.wyverno.files.hashs.FileHashInfo;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Path pathCloudFiles = Paths.get("./cloud-files.json");
    public static void main(String[] args) {
        try {

            Config config = loadConfig();//loading Configuration Application
            if (config == null) return;

            DropBoxAPI dropBoxAPI = connectToDropBoxAPI(config);//Connect to API

            FileCollectorVisitor fileCollectorVisitor = collectFilesApplication(config);

            HashSumFiles sumFiles = new HashSumFiles(config.getPathApplication(), fileCollectorVisitor.getFilesPath());

            ObjectMapper mapper = new ObjectMapper();

            List<FileHashInfo> filesApplication = sumFiles.getRelativizeRootFilesHashInfo();
            List<FileHashInfo> cloudFiles = mapper.readValue(pathCloudFiles.toFile(), new TypeReference<>() {});

            SyncCloudStorage syncCloudStorage = new SyncCloudStorage(filesApplication, cloudFiles);

            Set<FileHashInfo> deletedFiles = syncCloudStorage.getDeletedFiles();
            Set<FileHashInfo> addedFiles = syncCloudStorage.getAddedFiles();
            Set<Path> deletedFolders = syncCloudStorage.getDeletedFolders();
            Set<Path> cloudFolders = syncCloudStorage.getCloudFolders();
            for (FileHashInfo hashInfo : deletedFiles) {
                logger.info("Deleted:\nFile: {}\nHash: {}", hashInfo.getPathFile(),hashInfo.getHash());
            }

            for (FileHashInfo hashInfo : addedFiles) {
                logger.info("Added:\nFile: {}\nHash: {}", hashInfo.getPathFile(), hashInfo.getHash());
            }
            for (Path cloudFolder : cloudFolders) {
                logger.info("CloudFolder: {}", cloudFolder);
            }

            for (Path deletedFolder : deletedFolders) {
                logger.info("Deleted folder: {}", deletedFolder);
            }

            dropBoxAPI.deleteAllFromFolder("");

            dropBoxAPI.uploadFiles("", Collections.emptyList(),
                    fileCollectorVisitor
                            .getFolderPath()
                            .stream()
                            .map(path -> "/"+config.getPathApplication().relativize(path))
                            .filter(path -> !path.equals("/"))
                            .map(path -> path.replace("\\","/"))
                            .collect(Collectors.toList()));


        } catch (Throwable e) {
            logger.error("",e);
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
