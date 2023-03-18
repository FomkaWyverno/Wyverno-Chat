package ua.wyverno.twitch.api.authorization.account;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.ChatUserColor;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserChatColorList;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.github.twitch4j.pubsub.events.VideoPlaybackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.account.events.ChatMessageEventConsumer;
import ua.wyverno.twitch.api.authorization.account.events.RewardRedemptionEventConsumer;
import ua.wyverno.twitch.api.authorization.account.events.VideoPlaybackEventConsumer;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;

import java.util.*;

public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private static final Map<String, String> userNamesMapByIds = new HashMap<>(); // Кэш відображаємих нік-неймів
    private static final Map<String, Integer> followersCountMapByIds = new HashMap<>(); // Кєш кількість фолловерів по айді

    private static final Map<String, String> colorMapByIds = new HashMap<>(); // Кєш кольорів користувачів чату.
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
        logger.debug("Get Display Name by ID!");

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

    public int getFollowersCountByIds(String userId) {
        logger.debug("Get followers count by ID!");

        if (followersCountMapByIds.containsKey(userId)) {
            logger.debug("FollowersCount is has in map followersCount");
            return followersCountMapByIds.get(userId);
        }
        logger.debug("Followers count is not have in map followersCount!");
        int countFollowers = this.twitchClient.getHelix()
                .getChannelFollowers(this.accessToken,
                        this.getUserID(),
                        null,
                        null,
                        null).execute().getTotal();

        logger.info("Follower count: "+ countFollowers);
        logger.debug("Save count followers in map");
        followersCountMapByIds.put(userId,countFollowers);

        return countFollowers;
    }

    public String getUserChatColorByIds(String userId, String ifNullColor) { // ifNullColor - якщо у користовача нема коліра то встановлюему в кеш цей колір.
        logger.debug("User get color chat by id: " + userId);

        if (colorMapByIds.containsKey(userId)) {
            logger.debug("User color is have in map");

            String color = colorMapByIds.get(userId);

            logger.debug("User Color: " + color);

            return color;
        }
        logger.debug("User color not has in map colors");
        List<ChatUserColor> userColorList =
                this.twitchClient.getHelix()
                        .getUserChatColor(this.accessToken,
                                Collections.singletonList(userId))
                        .execute()
                        .getData();

        if (userColorList.isEmpty()) {
            logger.debug("User dont have color!");
            colorMapByIds.put(userId,ifNullColor);
            return ifNullColor;
        }

        logger.debug("User is has color.");

        String color = userColorList.get(0).getColor();
        logger.debug("User color: " + color);

        return color;
    }
    public void closeAccount() {
        if (this.twitchClient != null) {
            logger.info("Leave from Account!");
            this.twitchClient.close();
        }
    }
}
