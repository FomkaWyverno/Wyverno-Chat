package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.account.Account;

public class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
    private static Account instance;

    public static boolean isValidToken(String accessToken) { // Провіряємо через Twitch4J чи є валідним токен
        boolean isValidToken = new TwitchIdentityProvider(null,null, null)
                .isCredentialValid(
                new OAuth2Credential("twitch",accessToken))
                .orElse(false);
        logger.debug("Credential is valid -> " + isValidToken);
        return isValidToken;
    }

    public static Account registerAccount(String accessToken) throws AccessTokenNoLongerValidException {
        logger.info("Register new Account!");

        logger.debug("Check validate access token");

        if (!isValidToken(accessToken)) {
            logger.warn("Access Token not valid! Registration failed!");
            throw new AccessTokenNoLongerValidException();
        }

        instance = new Account(accessToken, ConfigHandler.getInstance().getClientID());

        return instance;
    }

    public static Account getAccountInstance() throws AccessTokenNoLongerValidException {
        logger.info("Get Account Instance | instance == null? -> " + (instance == null));
        if (instance != null) return instance;

        logger.warn("Account Instance == null!!");
        logger.warn("Try register account!");
        return registerAccount(ConfigHandler.getInstance().getAccessToken());
    }
}
