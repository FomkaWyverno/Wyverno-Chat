package ua.wyverno.dropbox.create.folder.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WrapperCreateFolderResult {

    @JsonProperty(".tag")
    private String tag;
    private WrapperMetadata metadata;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public WrapperMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(WrapperMetadata metadata) {
        this.metadata = metadata;
    }
}
