package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;
import ua.wyverno.twitch.api.chat.elements.Emote;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ChatMessageEventConsumer implements Consumer<ChannelMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    private static final String TEMPLATE;

    static {
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

        IRCMessageEvent ircMessageEvent = event.getMessageEvent();

        String username = ircMessageEvent.getTagValue("display-name").orElse(event.getUser().getName());
        String message = event.getMessage();
        String color = ircMessageEvent.getTagValue("color").orElse("#bb00ff");

        logger.info("MessageEvent: " + username + " > " + message + " | " + color);

        List<Emote> emoteList = new ArrayList<>();

        String finalMessage = message;
        ircMessageEvent.getTagValue("emotes")
                .map(emotes -> StringUtils.split(emotes, '/'))
                .ifPresent(emotes -> {
                    try {
                        for (String emoteStr : emotes) { // emotes
                            int indexDelim = emoteStr.indexOf(':');
                            String emoteId = emoteStr.substring(0,indexDelim);

                            String range = emoteStr.substring(indexDelim+1);
                            if (StringUtils.contains(range,',')) { // range World Emote in message
                                range = range.substring(0, StringUtils.indexOf(range,','));
                            }

                            int rangeIndexDelim = StringUtils.indexOf(range,'-');
                            int startRange = Integer.parseInt(range.substring(0,rangeIndexDelim));
                            int endRange = Integer.parseInt(range.substring(rangeIndexDelim+1));

                            String emoteName = finalMessage.substring(startRange,endRange+1);

                            emoteList.add(new Emote(emoteId,emoteName));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

        if (!emoteList.isEmpty()) {
            logger.debug("Start replace emoteName to img tag");

            for (Emote emote : emoteList) {
                message = message.replaceAll(emote.getEmoteName(), emote.toImgTag());
            }
        }

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
