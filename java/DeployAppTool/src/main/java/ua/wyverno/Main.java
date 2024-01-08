package ua.wyverno;

import com.dropbox.core.DbxException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.metadata.FolderMetadata;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Path pathCloudFiles = Paths.get("./cloud-files.json");

    private static final Config CONFIG;

    static {
        try {
            CONFIG = loadConfig();
        } catch (IOException e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            DropBoxAPI dropBoxAPI = connectToDropBoxAPI();

            MetadataContainer containerMetadata = dropBoxAPI.collectAllContentFromPath("");

            for (FolderMetadata metadata : containerMetadata.getFolderMetadataList()) {
                logger.info("Folder in application: {}",metadata.getPathDisplay());
            }
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    private static FileCollectorVisitor collectFilesApplication() throws IOException {
        logger.info("Start collect files!");
        FileCollectorVisitor fileCollectorVisitor = new FileCollectorVisitor();
        Files.walkFileTree(CONFIG.getPathApplication(), fileCollectorVisitor);
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

    private static HashSumFiles loadCloudFilesHashSum(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(path.toFile(), HashSumFiles.class);
    }
}
