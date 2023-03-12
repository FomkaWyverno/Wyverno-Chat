package ua.wyverno.twitch.api.chat;

public class Protocol {

    public TYPE type;

    public String content;

    public Protocol() {}

    public Protocol(TYPE type, String content) {
        this.type = type;
        this.content = content;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static enum TYPE {
        videoPlayback,html
    }
}
