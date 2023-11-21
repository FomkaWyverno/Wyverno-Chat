package ua.wyverno.twitch.api.chat.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.chat.elements.emote.EmoteElement;

import java.util.List;

public class MessageElement extends AbstractElement {

    private Logger logger = LoggerFactory.getLogger(MessageElement.class);

    private final String username;
    private final String username_color;
    private String message;

    private boolean isHighlightedMessage;


    public MessageElement(String username, String username_color, String message, boolean isHighlightedMessage) {
        super("html/overlay/elements/message.html");
        this.username = username;
        this.username_color = username_color;
        this.message = this.escapeMessage(message);
        this.isHighlightedMessage = isHighlightedMessage;
    }

    /**
     * Екранізовує повідомлення з чату, щоб при відображенні при відображенні його не було HTML тегів котрі могли бути у повідомленні у чаті
     * @param message повідомлення з чату
     * @return екранізоване повідомлення
     */
    private String escapeMessage(String message) {
        if (message == null) return null;

        StringBuilder escapedMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            switch (c) {
                case '&' -> escapedMessage.append("&amp;");
                case '<' -> escapedMessage.append("&lt;");
                case '>' -> escapedMessage.append("&gt;");
                case '"' -> escapedMessage.append("&quot;");
                case '\'' -> escapedMessage.append("&#039");
                default -> escapedMessage.append(c);
            }
        }

        return escapedMessage.toString();
    }

    public void replaceEmoteInMessage(List<EmoteElement> emotes) {
        logger.debug("Start replace emotes to HTML tags in message {}: {}", this.username, this.message);

        if (!emotes.isEmpty()) {
            logger.debug("Start replace emoteName to img tag");
            for (EmoteElement emoteElement : emotes) {
                logger.trace("Replace emoteName to image: \"" +emoteElement.getEmoteName() +"\"");
                this.message = this.message.replace(emoteElement.getEmoteName(), emoteElement.compileHTML().getHTML());
            }
        }
    }

    @Override
    public MessageElement compileHTML() {
        this.setTag("username", this.username);
        this.setTag("color-user", this.username_color);
        this.setTag("message", this.message);

        if (this.isHighlightedMessage) { // Якщо повідомлення виділене, то ставимо клас для повідомлення з виділенням
            this.setTag("isHighlight","message__container__content--highlight");
        } else {
            this.setTag("isHighlight", "");
        }

        return this;
    }
}
