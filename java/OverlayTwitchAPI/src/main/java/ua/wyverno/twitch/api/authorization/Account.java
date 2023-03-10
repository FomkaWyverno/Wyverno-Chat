package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static Account instance;
    private final TwitchClient twitchClient;
    private final User user;

    private Account(String accessToken,String clientID) {
        this.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withClientId(clientID)
                .withChatAccount(new OAuth2Credential("twitch",accessToken))
                .build();
        this.user = twitchClient
                .getHelix()
                .getUsers(accessToken,null,null)
                .execute()
                .getUsers()
                .get(0);
    }

    public static Account getInstance() {
        if (instance == null) instance = new Account(ConfigHandler.getInstance().getAccessToken(), ConfigHandler.getInstance().getClientID());
        return instance;
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    public String getDescription() {
        return this.user.getDescription();
    }

    public String getProfileImageURL() {
        return this.user.getProfileImageUrl();
    }

    public void closeAccount() {
        if (this.twitchClient != null) {
            logger.info("Leave from Account!");
            this.twitchClient.close();
        }
    }
}
