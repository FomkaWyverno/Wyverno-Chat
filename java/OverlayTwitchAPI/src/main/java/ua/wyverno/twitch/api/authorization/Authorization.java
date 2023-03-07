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

    public Authorization() {
        logger.debug("Config path = " +configFile);
        logger.debug("Config absolute path = " + configFile.toAbsolutePath().normalize());
        if (!isHasConfigFile()) {
            logger.debug("Not has config file");
            this.createConfigFile();
            try {
                HttpAuthServer httpAuthServer = new HttpAuthServer();
                httpAuthServer.start();
                httpAuthServer.askAuthorization();
            } catch (IOException e) {
                logger.error(ExceptionToString.getString(e));
            }

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
}
