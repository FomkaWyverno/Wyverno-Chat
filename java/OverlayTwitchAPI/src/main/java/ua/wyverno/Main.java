package ua.wyverno;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

public class Main {

    public static void main(String[] args) {
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                                                        .withEnableChat(true)
                                                        .build();
    }
}
