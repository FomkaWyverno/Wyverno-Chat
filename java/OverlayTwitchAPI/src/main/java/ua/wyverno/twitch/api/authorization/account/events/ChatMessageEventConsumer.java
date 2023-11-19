package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;
import ua.wyverno.twitch.api.chat.elements.MessageElement;

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

        MessageElement messageElementHtml = new MessageElement(username, color, messageContent);

        ircMessageEvent.getTagValue("emotes")
                .map(emotes -> StringUtils.split(emotes, '/'))
                .ifPresent(messageElementHtml::replaceEmoteInMessage);

        if (event.isHighlightedMessage()) { // Якщо виділине повідомленя тоді ставимо класс з виділліням
            messageElementHtml.setTag("isHighlight","message__container__content--highlight");
        } else {
            messageElementHtml.setTag("isHighlight","");
        }

        String htmlContext = messageElementHtml.getHtml();
        logger.debug("HTML Context\n"+htmlContext);
        ChatWebSocketServer.getInstance().messageEvent(new Protocol(Protocol.TYPE.messageHTML, htmlContext));
    }
}
