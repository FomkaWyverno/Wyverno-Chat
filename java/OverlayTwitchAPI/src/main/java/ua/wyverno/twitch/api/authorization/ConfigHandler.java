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

    private final Properties properties;

    private String accessToken;

    public ConfigHandler() throws IOException, AccessTokenNoLongerValidException {
        logger.debug("Config path = " + configFile);

        this.properties = new Properties();
        if (isHasConfigFile()) { // Якщо є конфіг файл то читаємо.
            logger.debug("Config file is exists");
            properties.load(Files.newBufferedReader(configFile));
            this.accessToken = properties.getProperty("access_token");
        } else { // Якщо нема конфіг файлу то створюємо.
            logger.debug("Not has config file");
            this.createConfigFile();
            properties.store(Files.newBufferedWriter(configFile),"");
            throw new AccessTokenNoLongerValidException(); // Викидуємо виключення що в нас нема токену.
        }
    }

    private boolean isHasConfigFile() {
        return Files.exists(configFile);
    }

    public void putAccessToken(String accessToken) throws IOException {
        this.accessToken = accessToken;
        this.properties.put("access_token",accessToken);
        this.properties.store(Files.newBufferedWriter(configFile),"");
    }

    private void createConfigFile() {
        try {
            Files.createDirectories(configFile.getParent()); // Створюэмо директорію до файлу з конфігом
        } catch (IOException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }
}
