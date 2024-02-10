package ua.wyverno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.cloud.SyncCloudStorage;
import ua.wyverno.files.cloud.SyncCloudStorageBuilder;
import ua.wyverno.files.hashs.FileMetadataNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static Config CONFIG;

    public static void main(String[] args) {
        try {
            CONFIG = loadConfig();
            if (CONFIG == null) return;
            DropBoxAPI dropBoxAPI = new DropBoxAPI(CONFIG.getAccessTokenDropBox());
            logger.info("Connect to DropBox API!");

            //FileCollectorVisitor localContentVisitor = collectLocalApplicationFilesAndFolders();
            //MetadataContainer dropboxContentContainer = dropBoxAPI.collectAllContentFromPathAsMetadataContainer("");
            FileMetadataNode cloudFolder = dropBoxAPI.collectRootContentAsCloudFileHashNode();
//            SyncCloudStorage syncCloudStorage = buildSyncCloudStorage(localContentVisitor, dropboxContentContainer);
//            syncCloudStorage.synchronizedWithCloudStorage(dropBoxAPI,CONFIG.getPathApplication());
//
//            DbxSharingLinkManager sharingLinkManager = new DbxSharingLinkManager(dropBoxAPI);
//            Set<SharedLinkMetadata> links = sharingLinkManager.getShareLinks();
//            links.forEach(link -> logger.info("Share path: {} url: {}", link.getPathLower(), link.getUrl()));
            logger.info(cloudFolder.toString());
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    private static SyncCloudStorage buildSyncCloudStorage(FileCollectorVisitor visitor, MetadataContainer allContentMetadata) throws IOException {
        List<FileMetadataNode> cloudFiles = null;//mapMetadataToFileHash(allContentMetadata);

        //HashSumFiles hashSumFiles = new HashSumFiles(CONFIG.getPathApplication(), visitor.getFilesPath());

        //List<FileMetadataNode> appFilesHashInfo = hashSumFiles.getFilesHash();
        Set<Path> appRelativizedPathFolders = visitor.getFolderPath()
                .stream()
                .map(pathFolder -> Paths.get("/").resolve(CONFIG.getPathApplication().relativize(pathFolder)))
                .collect(Collectors.toSet());
        appRelativizedPathFolders.remove(Paths.get("/"));

        return new SyncCloudStorageBuilder()
                .applicationAbsolutePathFiles(null)
                .applicationRelativizedPathFiles(null)
                .applicationFoldersRelativized(appRelativizedPathFolders)
                .cloudFiles(cloudFiles)
                .cloudFolders(allContentMetadata
                        .getFolderMetadataList()
                        .stream()
                        .map(metadata -> Paths.get(metadata.getPathDisplay()))
                        .collect(Collectors.toSet()))
                .build();
    }

    private static FileCollectorVisitor collectLocalApplicationFilesAndFolders() throws IOException {
        logger.info("Start collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        FileCollectorVisitor visitor = new FileCollectorVisitor(CONFIG.getPathApplication());
        Files.walkFileTree(CONFIG.getPathApplication(), visitor);
        logger.info("Finish collect files and folder from {}", CONFIG.getPathApplication().toAbsolutePath());
        return visitor;
    }

//    private static List<FileHash> mapMetadataToFileHash(MetadataContainer container) {
//        return container.getFileMetadataList()
//                .stream()
//                .map(metadata -> {
//                    Path path = Paths.get(metadata.getPathDisplay());
//                    String hash = metadata.getContentHash();
//                    return new FileHash(path,path, hash);
//                }).toList();
//    }

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
}
