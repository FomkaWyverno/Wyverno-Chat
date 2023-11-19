package ua.wyverno.twitch.api.chat.elements;


public abstract class AbstractElement {
    private String htmlTemplate;

    public AbstractElement(String path) {
        this.htmlTemplate = ElementResourceManager.getInstance().getTemplate(path);
    }

    public void setTag(String tag, String str) {
        this.htmlTemplate = this.htmlTemplate.replace("{" + tag + "}", str);
    }

    public String getHtml() {
        return this.htmlTemplate;
    }
}
