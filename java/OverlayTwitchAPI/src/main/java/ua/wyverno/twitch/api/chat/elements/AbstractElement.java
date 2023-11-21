package ua.wyverno.twitch.api.chat.elements;


public abstract class AbstractElement {
    private String HTML;

    public AbstractElement(String path) {
        this.HTML = ElementResourceManager.getInstance().getTemplate(path);
    }

    public void setTag(String tag, String str) {
        this.HTML = this.HTML.replace("{" + tag + "}", str);
    }

    /**
     * Має компілювати HTML елемент, щоб вірно відображати його.
     * @return повертає свій об'єкт
     */
    public abstract AbstractElement compileHTML();

    public String getHTML() {
        return this.HTML;
    }
}
