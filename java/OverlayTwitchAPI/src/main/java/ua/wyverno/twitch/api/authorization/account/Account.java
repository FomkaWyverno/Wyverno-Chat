package ua.wyverno.twitch.api.authorization.account;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.github.twitch4j.tmi.TwitchMessagingInterface;
import com.github.twitch4j.tmi.domain.Chatters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.account.events.ChatMessageEventConsumer;
import ua.wyverno.twitch.api.authorization.account.events.RewardRedemptionEventConsumer;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;

public class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);
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

        ChatWebSocketServer.getInstance();

        this.initEvents();
    }

    private void initEvents() {
        logger.info("Getting twitch chat!");
        TwitchChat twitchChat = this.twitchClient.getChat();

        logger.trace("Getting event manager!");

        this.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(null,this.user.getId());

        EventManager helixEventManager = twitchChat.getEventManager();

        helixEventManager.onEvent(ChannelMessageEvent.class, new ChatMessageEventConsumer());
        helixEventManager.onEvent(RewardRedeemedEvent.class, new RewardRedemptionEventConsumer());
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
