package ua.wyverno.twitch.api.chat.elements;

import ua.wyverno.twitch.api.authorization.account.events.ChatMessageEventConsumer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Emote {

    private static final String templateEMOTE;
    private final String emoteID;
    private final String emoteName;

    static {
        String tmp;
        try {
            byte[] bytes =
                    Objects.requireNonNull(Emote.class.getClassLoader()
                    .getResourceAsStream("html/overlay/elements/emote.html")).readAllBytes();
            tmp = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            templateEMOTE = "Not loading emote template";
            throw new RuntimeException(e);
        }
        templateEMOTE = tmp;
    }
    public Emote(String emoteID, String emoteName) {
        this.emoteID = emoteID;
        this.emoteName = emoteName;
    }

    public String getEmoteID() {
        return emoteID;
    }

    public String getEmoteName() {
        return emoteName;
    }

    public String getEmoteSrc() {
        return "https://static-cdn.jtvnw.net/emoticons/v2/" + this.emoteID + "/default/dark/1.0";
    }

    public String toImgTag() {
        return templateEMOTE.replace("{url-emote}",this.getEmoteSrc());
    }

    @Override
    public String toString() {
        return this.getEmoteSrc();
    }
}
