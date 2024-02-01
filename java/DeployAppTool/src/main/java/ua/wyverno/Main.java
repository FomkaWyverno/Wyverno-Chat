package ua.wyverno;

import com.dropbox.core.DbxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.cloud.SyncCloudStorage;
import ua.wyverno.files.cloud.SyncCloudStorageBuilder;
import ua.wyverno.files.hashs.FileHashInfo;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Config CONFIG;

    public static void main(String[] args) {
        try {
            CONFIG = loadConfig();
            if (CONFIG == null) return;
            DropBoxAPI dropBoxAPI = connectToDropBoxAPI();
            logger.info("Connect to DropBox API!");

            FileCollectorVisitor visitor = collectLocalFileAndFolders();
            logger.info("Start collect all content from root path in DropBox");
            MetadataContainer container = dropBoxAPI.collectAllContentFromPath("");
            
            SyncCloudStorage syncCloudStorage = buildSyncCloudStorage(visitor, container);
            syncCloudStorage.synchronizedWithCloudStorage(dropBoxAPI,CONFIG.getPathApplication());
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    private static SyncCloudStorage buildSyncCloudStorage(FileCollectorVisitor visitor, MetadataContainer allContentMetadata) throws IOException {
        List<FileHashInfo> cloudFiles = mapMetadataToFileHashInfo(allContentMetadata);

        HashSumFiles hashSumFiles = new HashSumFiles(CONFIG.getPathApplication(), visitor.getFilesPath());

        List<FileHashInfo> appFilesHashInfo = hashSumFiles.getFilesHashInfo();
        List<FileHashInfo> appRelativizedPathFiles = hashSumFiles.getRelativizeRootFilesHashInfo();
        Set<Path> appRelativizedPathFolders = visitor.getFolderPath()
                .stream()
                .map(pathFolder -> Paths.get("/").resolve(CONFIG.getPathApplication().relativize(pathFolder)))
                .collect(Collectors.toSet());
        appRelativizedPathFolders.remove(Paths.get("/"));

        return new SyncCloudStorageBuilder()
                .applicationAbsolutePathFiles(appFilesHashInfo)
                .applicationRelativizedPathFiles(appRelativizedPathFiles)
                .applicationFoldersRelativized(appRelativizedPathFolders)
                .cloudFiles(cloudFiles)
                .cloudFolders(allContentMetadata
                        .getFolderMetadataList()
                        .stream()
                        .map(metadata -> Paths.get(metadata.getPathDisplay()))
                        .collect(Collectors.toSet()))
                .createSyncCloudStorage();
    }

    private static FileCollectorVisitor collectLocalFileAndFolders() throws IOException {
        logger.info("Start collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        FileCollectorVisitor visitor = new FileCollectorVisitor();
        Files.walkFileTree(CONFIG.getPathApplication(), visitor);
        logger.info("Finish collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        return visitor;
    }

    private static List<FileHashInfo> mapMetadataToFileHashInfo(MetadataContainer container) {
        return container.getFileMetadataList()
                .stream()
                .map(metadata -> {
                    Path path = Paths.get(metadata.getPathDisplay());
                    String hash = metadata.getContentHash();
                    return new FileHashInfo(path, hash);
                }).toList();
    }
    private static Config loadConfig() throws IOException {
        Path pathConfig = Paths.get("./setting.properties");
        try {
            Config config = new Config(pathConfig);
            if (config.getPathApplication() == null) {
                logger.warn("Config-file not has path to Application files!");
                return null;
            }
            return config;
        } catch (FileNotFoundException e) {
            logger.info("Config-file not found! Empty config file created!");
            Config.createDefaultPropertiesFile(pathConfig);
            return null;
        }
    }
    private static DropBoxAPI connectToDropBoxAPI() throws DbxException, IOException {
        String accessTokenDbX = CONFIG.getAccessTokenDropBox();
        if (Objects.isNull(accessTokenDbX) || !new DropBoxAPI(accessTokenDbX).isValidAccessToken()) {
            logger.debug("Access Token is not valid or not present! Updating...");
            CONFIG.updateDbxAccessToken();
            accessTokenDbX = CONFIG.getAccessTokenDropBox();
        } else {
            logger.debug("Access Token is valid!");
        }

        return new DropBoxAPI(accessTokenDbX);
    }
}
