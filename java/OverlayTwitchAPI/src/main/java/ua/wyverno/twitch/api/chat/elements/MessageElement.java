package ua.wyverno.twitch.api.chat.elements;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageElement extends AbstractElement {

    private Logger logger = LoggerFactory.getLogger(MessageElement.class);

    private final String username;
    private final String username_color;
    private String message;


    public MessageElement(String username, String username_color, String message) {
        super("html/overlay/elements/message.html");
        this.username = username;
        this.username_color = username_color;
        this.message = message;

        this.setTag("username", this.username);
        this.setTag("color-user", this.username_color);
        this.setTag("message", this.message);
    }

    public void replaceEmoteInMessage(String[] emotes) {
        logger.debug("Start replace emotes to HTML tags in message {}: {}", this.username, this.message);
        List<EmoteElement> emoteElementList = new ArrayList<>();
        try {
            for (String emoteStr : emotes) { // emotes
                int indexDelim = emoteStr.indexOf(':');
                String emoteId = emoteStr.substring(0,indexDelim);

                String range = emoteStr.substring(indexDelim+1);
                if (StringUtils.contains(range,',')) { // range World EmoteElement in message
                    range = range.substring(0, StringUtils.indexOf(range,','));
                }

                int rangeIndexDelim = StringUtils.indexOf(range,'-');
                int startRange = Integer.parseInt(range.substring(0,rangeIndexDelim));
                int endRange = Integer.parseInt(range.substring(rangeIndexDelim+1));

                String emoteName = this.message.substring(startRange,endRange+1);

                emoteElementList.add(new EmoteElement(emoteId,emoteName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!emoteElementList.isEmpty()) {
            logger.debug("Start replace emoteName to img tag");

            for (EmoteElement emoteElement : emoteElementList) {
                this.message = this.message.replace(emoteElement.getEmoteName(), emoteElement.getHtml());
            }
        }
    }
}
