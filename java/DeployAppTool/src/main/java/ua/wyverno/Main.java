package ua.wyverno;

import com.dropbox.core.DbxException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {

            Config config = loadConfig();//loading Configuration Application
            if (config == null) return;

            DropBoxAPI dropBoxAPI = connectToDropBoxAPI(config);//Connect to API

            FileCollectorVisitor fileCollectorVisitor = collectFilesApplication(config);

            HashSumFiles sumFiles = hashSumApplication(config,fileCollectorVisitor);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sumFiles);
            logger.info("{}\n Generated json with Hash-Sum for Application files",json);

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

    private static HashSumFiles hashSumApplication(Config config, FileCollectorVisitor fileCollector) throws IOException {
        HashSumFiles hashSumFiles = new HashSumFiles(config.getPathApplication(), fileCollector.getFilesPath());
        logger.info("Start hashing application files!");
        hashSumFiles.toHashFiles();
        return hashSumFiles;
    }
}
