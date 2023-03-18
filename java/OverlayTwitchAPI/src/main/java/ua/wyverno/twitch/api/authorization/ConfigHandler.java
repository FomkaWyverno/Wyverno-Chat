package ua.wyverno.twitch.api.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);

    private static final Path configFile = Paths.get("./twitch/api/config/account.properties").toAbsolutePath().normalize();

    private static ConfigHandler instance;

    private final Properties properties;

    private volatile String accessToken;

    private static final String clientID = "znxb14or3tj0cm6e1pixh7zijlsgua";

    private ConfigHandler() throws IOException {
        logger.debug("Config path = " + configFile);

        this.properties = new Properties();
        if (isHasConfigFile()) { // Якщо є конфіг файл то читаємо.
            logger.debug("Config file is exists");
            properties.load(Files.newBufferedReader(configFile));
            this.accessToken = properties.getProperty("access_token");
        } else { // Якщо нема конфіг файлу то створюємо.
            logger.debug("Not has config file");
            this.createDirectoryForConfig();
            properties.store(Files.newBufferedWriter(configFile),"");
        }
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            try {
                logger.info("Create ConfigHandler");
                instance = new ConfigHandler();
            } catch (IOException e) {
                logger.error(ExceptionToString.getString(e));
            }
        }
        return instance;
    }

    public String getClientID() {
        return clientID;
    }

    private boolean isHasConfigFile() {
        return Files.exists(configFile);
    }

    public void putAccessToken(String accessToken) throws IOException {
        this.accessToken = accessToken;
        this.properties.put("access_token",accessToken);
        this.properties.store(Files.newBufferedWriter(configFile),"");
    }

    public String getAccessToken() {
        return this.accessToken;
    }
    public boolean isValidAccessToken() {
        logger.debug("AccessToken is null? - " + (this.accessToken == null));
        if (this.accessToken==null) return false;

        logger.debug("AccessToken isEmpty? - " + this.accessToken.isEmpty());
        if (this.accessToken.isEmpty()) return false;

        if (Authorization.isValidToken(this.accessToken)) {
            logger.debug("Access Token is valid");
            return true;
        }
        logger.debug("Access Token is not valid");
        return false;
    }
    private void createDirectoryForConfig() {
        try {
            Files.createDirectories(configFile.getParent()); // Створюэмо директорію до файлу з конфігом
        } catch (IOException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }


}
