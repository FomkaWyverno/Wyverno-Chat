package ua.wyverno;

import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.metadata.FileMetadata;
import ua.wyverno.dropbox.metadata.FolderMetadata;
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
import java.util.stream.Stream;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Config CONFIG;

    public static void main(String[] args) {
        try {
            CONFIG = loadConfig();
            if (CONFIG == null) return;
            DropBoxAPI dropBoxAPI = connectToDropBoxAPI();
            logger.info("Connect to DropBox API!");

//            FileCollectorVisitor localContentVisitor = collectLocalApplicationFilesAndFolders();
//            MetadataContainer dropboxContentContainer = dropBoxAPI.collectAllContentFromPath("");
//
//            SyncCloudStorage syncCloudStorage = buildSyncCloudStorage(localContentVisitor, dropboxContentContainer);
//            syncCloudStorage.synchronizedWithCloudStorage(dropBoxAPI,CONFIG.getPathApplication());
//
            MetadataContainer dropboxContentAfterUpload = dropBoxAPI.collectAllContentFromPath("");
            List<SharedLinkMetadata> sharedLinkMetadataList = dropBoxAPI.listSharedLinks();

            Set<String> setPathWithoutShareLink = Stream.concat(
                            dropboxContentAfterUpload.getFolderMetadataList()
                                                        .stream()
                                                        .map(FolderMetadata::getPathLower),
                            dropboxContentAfterUpload.getFileMetadataList()
                                                        .stream()
                                                        .map(FileMetadata::getPathLower))
                            .filter(cloudPathLower -> sharedLinkMetadataList
                                                        .stream()
                                                        .noneMatch(sharedLinkMetadata ->
                                                                sharedLinkMetadata.getPathLower().equals(cloudPathLower)))
                            .collect(Collectors.toSet());


            setPathWithoutShareLink.forEach(path -> logger.info("Path without share link: {}", path));

            Set<SharedLinkMetadata> sharedLinkMetadataSet = setPathWithoutShareLink.
                    stream()
                    .map(path -> {
                        try {
                            return dropBoxAPI.createSharedLink(path);
                        } catch (DbxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());

            sharedLinkMetadataSet.addAll(sharedLinkMetadataList);

            sharedLinkMetadataSet.forEach(sharedLink -> logger.info("SharedLink: {} Link: {}", sharedLink.getPathLower(), sharedLink.getUrl()));
        } catch (InvalidAccessTokenException e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(e.getMessage());
                boolean isExpiredAccessToken = jsonNode
                        .path("error")
                        .path(".tag")
                        .asText()
                        .equals("expired_access_token");

                if (isExpiredAccessToken) {
                    logger.warn("Access Token is expired. Restart program!");
                }
            } catch (JacksonException ex) {
                logger.error("Error in parsing JSON!", ex);
            }

        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    private static SyncCloudStorage buildSyncCloudStorage(FileCollectorVisitor visitor, MetadataContainer allContentMetadata) throws IOException {
        List<FileHashInfo> cloudFiles = mapMetadataToFileHashInfos(allContentMetadata);

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

    private static FileCollectorVisitor collectLocalApplicationFilesAndFolders() throws IOException {
        logger.info("Start collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        FileCollectorVisitor visitor = new FileCollectorVisitor();
        Files.walkFileTree(CONFIG.getPathApplication(), visitor);
        logger.info("Finish collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        return visitor;
    }

    private static List<FileHashInfo> mapMetadataToFileHashInfos(MetadataContainer container) {
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
