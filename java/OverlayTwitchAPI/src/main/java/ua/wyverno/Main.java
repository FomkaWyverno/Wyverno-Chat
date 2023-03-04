package ua.wyverno;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.ChatBadge;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                                                        .withEnableChat(true)
                                                        .build();

        twitchClient.getChat().joinChannel("Fomka_Wyverno");


        twitchClient.getEventManager().onEvent(IRCMessageEvent.class, event -> {
            Optional<String> optionalMessage = event.getMessage();
            Optional<String> optionalClientName = event.getClientName();
            if (optionalClientName.isPresent()&&optionalMessage.isPresent()) {
                System.out.printf("%s: %s%n",optionalClientName.get(),optionalMessage.get());
            }
        });
    }
}
