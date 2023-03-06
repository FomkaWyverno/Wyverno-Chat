package ua.wyverno;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.DefaultOAuth2IdentityProvider;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.TwitchAuth;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.pubsub.events.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        // Twitch API credentials
        String clientId = args[0];
        String clientSecret = args[1];
        //String accessToken = "your_access_token_here";

        CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
        // Create an authenticator and set up the client builder
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableChat(true)
                .withEnableHelix(true)
                .withEnablePubSub(true)
                .withChatCommandsViaHelix(false)
                .build();

        twitchClient.getChat().joinChannel("Fomka_Wyverno");

        twitchClient.getEventManager().onEvent(IRCMessageEvent.class, event -> {
            Optional<String> message = event.getMessage();

            message.ifPresent(s -> System.out.printf("%s: %s%n", event.getUserName(), s));
        });

        String userID = twitchClient.getHelix().getUsers(null,null, Collections.singletonList("Fomka_Wyverno")).execute().getUsers().get(0).getId();

        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(null,userID);

        twitchClient.getEventManager().onEvent(PointsEarnedEvent.class, System.out::println);
        twitchClient.getEventManager().onEvent(ClaimAvailableEvent.class, System.out::println);
        twitchClient.getEventManager().onEvent(ClaimClaimedEvent.class, System.out::println);
        twitchClient.getEventManager().onEvent(PointsSpentEvent.class, System.out::println);
        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, System.out::println);
    }
}
