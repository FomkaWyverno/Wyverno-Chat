package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;
import ua.wyverno.twitch.api.chat.elements.MessageElement;
import ua.wyverno.twitch.api.chat.elements.emote.EmoteElement;
import ua.wyverno.twitch.api.chat.elements.emote.EmoteParser;

import java.util.List;
import java.util.function.Consumer;

public class ChatMessageEventConsumer implements Consumer<ChannelMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    @Override
    public void accept(ChannelMessageEvent event) {
        if (event.getCustomRewardId().isPresent()) {
            logger.info("Reward message ignore consumer event!");
            return;
        }

        IRCMessageEvent ircMessageEvent = event.getMessageEvent();

        String username = ircMessageEvent.getTagValue("display-name").orElse(event.getUser().getName());
        String messageContent = event.getMessage();
        String color = ircMessageEvent.getTagValue("color").orElse("#bb00ff");

        logger.info("MessageEvent: " + username + " > " + messageContent + " | " + color);
        MessageElement messageElementHtml = new MessageElement(username, color, messageContent, event.isHighlightedMessage());


        ircMessageEvent.getTagValue("emotes")
                .ifPresent(emotesString -> {
                    List<EmoteElement> emoteElementList = EmoteParser.parseEmoteListFromString(emotesString, messageContent);
                    messageElementHtml.replaceEmoteInMessage(emoteElementList);
                });


        String htmlContext = messageElementHtml.compileHTML().getHTML();
        logger.debug("HTML Context\n"+htmlContext);
        ChatWebSocketServer.getInstance().messageEvent(new Protocol(Protocol.TYPE.messageHTML, htmlContext));
    }
}
