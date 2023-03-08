package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
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
import java.util.Optional;
import java.util.Properties;

public class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
    private static final Path configFile = Paths.get("./twitch/api/config/account.properties").toAbsolutePath().normalize();

    private final String accessToken;

    public Authorization() throws Exception {
        logger.debug("Config path = " +configFile);

        Properties p = new Properties();
        if (!isHasConfigFile()) { // Якщо нема конфиг файла.
            logger.debug("Not has config file");
            this.createConfigFile();
            this.accessToken = getAccessTokenFromUser();
            p.put("access_token", this.accessToken);
            p.store(Files.newBufferedWriter(configFile),"");
        } else { // Якщо є конфіг файл то читаємо.
            logger.debug("Config file is exists");
            p.load(Files.newBufferedReader(configFile));
            this.accessToken = p.getProperty("access_token");
        }
    }

    private String getAccessTokenFromUser() throws Exception {
        try {
            HttpAuthServer httpAuthServer = new HttpAuthServer();
            httpAuthServer.start();
            logger.debug("Ask access token");
            httpAuthServer.askAuthorization();
            String accessToken = httpAuthServer.getResultAsk().getAccessToken();
            logger.info("Access token: " + accessToken);
            return accessToken;
        } catch (IOException e) {
            logger.error(ExceptionToString.getString(e));
        }
        return null;
    }

    private boolean isValidToken(String accessToken) {
        return new TwitchIdentityProvider(null,null, null)
                .isCredentialValid(
                new OAuth2Credential("twitch",this.accessToken))
                .orElse(false);
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
