package ua.wyverno.twitch.api.authorization.account;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.github.twitch4j.pubsub.events.VideoPlaybackEvent;
import com.github.twitch4j.tmi.TwitchMessagingInterface;
import com.github.twitch4j.tmi.domain.Chatters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.account.events.ChatMessageEventConsumer;
import ua.wyverno.twitch.api.authorization.account.events.RewardRedemptionEventConsumer;
import ua.wyverno.twitch.api.authorization.account.events.VideoPlaybackEventConsumer;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private static final Map<String, String> userNamesMapByIds = new HashMap<>(); // Кэш

    private final String accessToken;

    private final TwitchClient twitchClient;
    private final User user;

    public Account(String accessToken,String clientID) {
        this.twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withClientId(clientID)
                .withEnablePubSub(true)
                .withEnableTMI(true)
                .withChatCommandsViaHelix(false)
                .withChatAccount(new OAuth2Credential("twitch",accessToken))
                .build();
        this.user = twitchClient
                .getHelix()
                .getUsers(accessToken,null,null)
                .execute()
                .getUsers()
                .get(0);

        this.accessToken = accessToken;

        ChatWebSocketServer.getInstance();

        logger.info("Initialization events Account!");
        this.initEvents();
    }

    private void initEvents() {
        logger.info("Getting twitch chat!");
        TwitchChat twitchChat = this.twitchClient.getChat();

        logger.trace("Getting event manager!");

        this.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(null,this.user.getId());
        this.twitchClient.getPubSub().listenForVideoPlaybackEvents(null,this.user.getId());

        EventManager helixEventManager = twitchChat.getEventManager();

        helixEventManager.onEvent(ChannelMessageEvent.class, new ChatMessageEventConsumer());
        helixEventManager.onEvent(RewardRedeemedEvent.class, new RewardRedemptionEventConsumer());
        helixEventManager.onEvent(VideoPlaybackEvent.class, new VideoPlaybackEventConsumer());
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    public String getUserID() {
        return this.user.getId();
    }

    public String getUsername() {
        return this.user.getLogin();
    }

    public String getLogin() {
        return this.user.getLogin();
    }

    public String getDescription() {
        return this.user.getDescription();
    }

    public String getProfileImageURL() {
        return this.user.getProfileImageUrl();
    }

    public Optional<String> getUsernameByIds(String userId) {
        logger.debug("Get User by ID!");

        if (userNamesMapByIds.containsKey(userId)) {
            logger.debug("Username is has in map userNamesMapByIds");
            return Optional.ofNullable(userNamesMapByIds.get(userId));
        }
        logger.debug("Username is not have in map userNAmesMapByIds");
        List<User> userList = twitchClient.getHelix()
                .getUsers(this.accessToken,Collections.singletonList(userId),null)
                .execute()
                .getUsers();
        if (userList.isEmpty()) {
            logger.debug("Username not search in Twitch!");
            return Optional.empty();
        }

        String username = userList.get(0).getDisplayName();

        logger.debug("User found! userId: " + userId + " DisplayName: " + username);
        userNamesMapByIds.put(userId,username);

        return Optional.ofNullable(username);
    }

    public void closeAccount() {
        if (this.twitchClient != null) {
            logger.info("Leave from Account!");
            this.twitchClient.close();
        }
    }
}
