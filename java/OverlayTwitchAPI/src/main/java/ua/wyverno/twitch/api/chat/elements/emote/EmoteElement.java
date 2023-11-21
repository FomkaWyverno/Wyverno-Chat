package ua.wyverno.twitch.api.chat.elements;

public class EmoteElement extends AbstractElement {
    private final String emoteID;
    private final String emoteName;
    public EmoteElement(String emoteID, String emoteName) {
        super("html/overlay/elements/emote.html");
        this.emoteID = emoteID;
        this.emoteName = emoteName;
    }

    public String getEmoteID() {
        return emoteID;
    }

    public String getEmoteName() {
        return emoteName;
    }

    private String getEmoteSrc() {
        return "https://static-cdn.jtvnw.net/emoticons/v2/" + this.emoteID + "/default/dark/1.0";
    }

    @Override
    public void compileHtml() {
        this.setTag("url-tag", this.getEmoteSrc());
    }
    @Override
    public String toString() {
        return this.getEmoteSrc();
    }
}
