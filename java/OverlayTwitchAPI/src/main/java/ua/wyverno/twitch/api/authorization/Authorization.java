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

    private OAuth2Credential auth2Credential;
    private final String accessToken;

    public Authorization(String accessToken) throws AccessTokenNoLongerValidException {
        this.accessToken = accessToken;
        if (accessToken == null || accessToken.isEmpty() || !isValidToken()) {
            throw new AccessTokenNoLongerValidException();
        }
    }

    protected boolean isValidToken() { // Провіряємо через Twitch4J чи є валідним токен
        boolean isValidToken = new TwitchIdentityProvider(null,null, null)
                .isCredentialValid(
                new OAuth2Credential("twitch",this.accessToken))
                .orElse(false);
        logger.debug("Credential is valid -> " + isValidToken);
        return isValidToken;
    }

    public OAuth2Credential getAccount() {
        if (this.auth2Credential == null) this.auth2Credential = new OAuth2Credential("twitch", this.accessToken);
        return this.auth2Credential;
    }
}
