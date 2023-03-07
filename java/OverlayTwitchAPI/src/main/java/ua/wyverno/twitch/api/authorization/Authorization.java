package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
    private static final Path configFile = Paths.get("./twitch/api/config/account.properties").toAbsolutePath().normalize();

    private static final String authURL = "https://id.twitch.tv/oauth2/authorize?client_id=znxb14or3tj0cm6e1pixh7zijlsgua&redirect_uri=http%3A%2F%2Flocalhost%3A2828/access&response_type=token&scope=channel%3Aread%3Aredemptions+chat%3Aread";

    public Authorization() {
        logger.debug("Config path = " +configFile);
        logger.debug("Config absolute path = " + configFile.toAbsolutePath().normalize());
        if (!isHasConfigFile()) {
            logger.debug("Not has config file");
            this.createConfigFile();

            this.askAuthorization();
        }
    }
    public OAuth2Credential getAccount() {
        return null;
    }

    private boolean isHasConfigFile() {
        return Files.exists(configFile);
    }

    private void createConfigFile() {
        try {
            Files.createDirectories(configFile.getParent()); // Створюэмо директорію до файлу з конфігом
        } catch (IOException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    private void askAuthorization() {
        try {
            Desktop.getDesktop().browse(new URL(authURL).toURI());
        } catch (IOException | URISyntaxException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }
}
