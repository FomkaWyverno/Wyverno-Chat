package ua.wyverno.dropbox.create.folder.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WrapperMetadata {
    @JsonProperty(".tag")
    private String tag;
    private String name;
    private String id;
    private String path_lower;
    private String path_display;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath_lower() {
        return path_lower;
    }

    public void setPath_lower(String path_lower) {
        this.path_lower = path_lower;
    }

    public String getPathDisplay() {
        return path_display;
    }

    public void setPath_display(String path_display) {
        this.path_display = path_display;
    }
}
