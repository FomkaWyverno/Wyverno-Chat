package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class ChatMessageEventConsumer implements Consumer<ChannelMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    private static final String TEMPLATE;

    static {
        File templateMessage = new File("html/overlay/elements/message.html");
        String tmp;
        try {
            tmp = Files.readString(templateMessage.toPath());
        } catch (IOException e) {
            tmp = "BAD LOAD TEMPLATE!";
        }

        TEMPLATE = tmp;
    }


    @Override
    public void accept(ChannelMessageEvent event) {
        String username = event.getMessageEvent().getTagValue("display-name").orElse(event.getUser().getName());
        String message = event.getMessage();
        String color = event.getMessageEvent().getTagValue("color").orElse("NO COLOR");

        logger.info("MessageEvent: " + username + " > " + message + " | " + color);

        String htmlContext = getHTMLContext(username, message);

        logger.debug("HTML Context\n"+htmlContext);

        ChatWebSocketServer.getInstance().messageEvent(new Protocol(Protocol.TYPE.html, htmlContext));
    }

    private String getHTMLContext(String username, String message) {
        String htmlContext = TEMPLATE;

        htmlContext = htmlContext.replace("{username}",username);
        htmlContext = htmlContext.replace("{content}",message);

        return htmlContext;
    }
}
