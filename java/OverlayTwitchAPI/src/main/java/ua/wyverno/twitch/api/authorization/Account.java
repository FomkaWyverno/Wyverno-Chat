package ua.wyverno.twitch.api.authorization;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;

public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private final TwitchClient twitchClient;
    private final User user;

    protected Account(String accessToken,String clientID) {
        this.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withClientId(clientID)
                .withChatAccount(new OAuth2Credential("twitch",accessToken))
                .build();
        this.user = twitchClient
                .getHelix()
                .getUsers(accessToken,null,null)
                .execute()
                .getUsers()
                .get(0);

        ChatWebSocketServer.getInstance();
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
