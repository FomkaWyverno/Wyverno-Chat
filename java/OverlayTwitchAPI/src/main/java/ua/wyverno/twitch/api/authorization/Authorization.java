package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpServer;
import ua.wyverno.util.ExceptionToString;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
    private static final Path configFile = Paths.get("./twitch/api/config/account.properties").toAbsolutePath().normalize();

    private final OAuth2Credential auth2Credential;

    public Authorization() throws Exception {
        logger.debug("Config path = " +configFile);

        Properties p = new Properties();
        String accessToken;
        if (!isHasConfigFile()) { // Якщо нема конфиг файла.
            logger.debug("Not has config file");
            this.createConfigFile();
            accessToken = getAccessTokenFromUser();
            p.put("access_token", accessToken);
            p.store(Files.newBufferedWriter(configFile),"");
        } else { // Якщо є конфіг файл то читаємо.
            logger.debug("Config file is exists");
            p.load(Files.newBufferedReader(configFile));
            accessToken = p.getProperty("access_token");
        }
        if (accessToken == null || accessToken.isEmpty() || !isValidToken(accessToken)) {
            throw new AccessTokenNoLongerValidException();
        }
        this.auth2Credential = new OAuth2Credential("twitch", accessToken);
    }

    private String getAccessTokenFromUser() throws Exception {
        try {
            HttpServer httpServer = new HttpServer();
            httpServer.start();
            logger.debug("Ask access token");
            httpServer.askAuthorization();
            String accessToken = httpServer.getResultAsk().getAccessToken();
            logger.info("Access token: " + accessToken);
            return accessToken;
        } catch (IOException e) {
            logger.error(ExceptionToString.getString(e));
        }
        return null;
    }

    private boolean isValidToken(String accessToken) {
        boolean isValidToken = new TwitchIdentityProvider(null,null, null)
                .isCredentialValid(
                new OAuth2Credential("twitch",accessToken))
                .orElse(false);
        logger.debug("Credential is valid -> " + isValidToken);
        return isValidToken;
    }

    public OAuth2Credential getAccount() {
        return this.auth2Credential;
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
