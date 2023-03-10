package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
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
}
