package ua.wyverno.twitch.api.chat;

public class VideoPlaybackProtocol {

    public TYPE type;
    public String content;

    public VideoPlaybackProtocol(TYPE type, String content) {
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

    public enum TYPE {
        STREAM_UP, STREAM_DOWN, VIEW_COUNT
    }
}
