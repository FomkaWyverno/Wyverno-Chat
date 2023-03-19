package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;

public class ChatMessageEventConsumer implements Consumer<ChannelMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    private static final String TEMPLATE;

    static {
        File templateMessage = new File("html/overlay/elements/message.html");
        String tmp;
        try {
            byte[] bytes = Objects.requireNonNull(ChatMessageEventConsumer.class.getClassLoader()
                    .getResourceAsStream("html/overlay/elements/message.html")).readAllBytes();
            tmp = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            tmp = "BAD LOAD TEMPLATE!";
        }

        TEMPLATE = tmp;
    }


    @Override
    public void accept(ChannelMessageEvent event) {
        if (event.getCustomRewardId().isPresent()) {
            logger.info("Reward message ignore consumer event!");
            return;
        }

        String username = event.getMessageEvent().getTagValue("display-name").orElse(event.getUser().getName());
        String message = event.getMessage();
        String color = event.getMessageEvent().getTagValue("color").orElse("#bb00ff");

        logger.info("MessageEvent: " + username + " > " + message + " | " + color);

        String htmlContext = getHTMLContext(username, message, color);

        if (!event.isHighlightedMessage()) { // Якщо не виділине повідомленя тоді прибираємо класс з виділліням
            htmlContext = htmlContext.replace("message__container__content--highlight","");
        }

        logger.debug("HTML Context\n"+htmlContext);

        ChatWebSocketServer.getInstance().messageEvent(new Protocol(Protocol.TYPE.messageHTML, htmlContext));
    }

    private String getHTMLContext(String username, String message, String color) {
        String htmlContext = TEMPLATE;

        htmlContext = htmlContext.replace("{color-user}",color);
        htmlContext = htmlContext.replace("{username}",username);
        htmlContext = htmlContext.replace("{message}",message);

        return htmlContext;
    }
}
