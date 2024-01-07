package ua.wyverno.dropbox.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.wyverno.json.jackson.deserializer.FolderMetadataDeserializer;

@JsonDeserialize(using = FolderMetadataDeserializer.class)
public class FolderMetadata {
    private String name;
    private String id;
    private String pathLower;
    private String pathDisplay;

    private FolderMetadata(String name, String id, String pathLower, String pathDisplay) {
        this.name = name;
        this.id = id;
        this.pathLower = pathLower;
        this.pathDisplay = pathDisplay;
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

    public String getPathLower() {
        return pathLower;
    }

    public void setPathLower(String pathLower) {
        this.pathLower = pathLower;
    }

    public String getPathDisplay() {
        return pathDisplay;
    }

    public void setPathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
    }

    public static class Builder {

        private String name;
        private String id;
        private String pathLower;
        private String pathDisplay;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder pathLower(String pathLower) {
            this.pathLower = pathLower;
            return this;
        }

        public Builder pathDisplay(String pathDisplay) {
            this.pathDisplay = pathDisplay;
            return this;
        }

        public FolderMetadata createFolderMetadata() {
            return new FolderMetadata(name, id, pathLower, pathDisplay);
        }
    }
}
