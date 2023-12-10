package ua.wyverno;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.config.Config;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.auth.DropBoxAuthServer;
import ua.wyverno.files.FileCollectorVisitor;
import ua.wyverno.files.hashs.HashSumFiles;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Path pathConfig = Paths.get("./setting.properties");
            Config config;
            try {
                config = new Config(pathConfig);
            } catch (FileNotFoundException e) {
                logger.info("Config-file not found! Empty config file created!");
                Config.createDefaultPropertiesFile(pathConfig);
                return;
            }
            Path applicationFolder = config.getPathApplication();


            String accessTokenDbX = config.getAccessTokenDropBox();

            if (Objects.isNull(accessTokenDbX) || !new DropBoxAPI(accessTokenDbX).isValidAccessToken()) {
                logger.debug("Access Token is not valid or not present! Updating...");
                config.updateDbxAccessToken();
                accessTokenDbX = config.getAccessTokenDropBox();
            } else {
                logger.debug("Access Token is valid!");
            }

            DropBoxAPI dropBoxAPI = new DropBoxAPI(accessTokenDbX);


            logger.info("Start collect files!");
            FileCollectorVisitor fileCollectorVisitor = new FileCollectorVisitor();
            Files.walkFileTree(applicationFolder, fileCollectorVisitor);
            logger.info("End collect files!");

            ObjectMapper mapper = new ObjectMapper();
            HashSumFiles sumFiles = new HashSumFiles(applicationFolder, fileCollectorVisitor.getFilesPath());
            logger.info("Start hashing files!");
            sumFiles.toHashFiles();

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sumFiles);

            logger.info("{}\n Generated json with Hash-Sum",json);


            dropBoxAPI.deleteAllFromFolder("");
        } catch (Throwable e) {
            logger.error("",e);
        }
    }
}
