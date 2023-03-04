package ua.wyverno;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Twitch API credentials
        String clientId = args[0];
        String clientSecret = args[1];
        //String accessToken = "your_access_token_here";

        // Create an authenticator and set up the client builder
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withClientSecret(clientSecret)
                .build();

        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, System.out::println);
    }
}
