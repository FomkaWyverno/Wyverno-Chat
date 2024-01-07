package ua.wyverno.dropbox.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.wyverno.json.jackson.deserializer.FileMetadataDeserializer;

import java.time.LocalDateTime;

@JsonDeserialize(using = FileMetadataDeserializer.class)
public class FileMetadata {
    private String name;
    private String id;
    private LocalDateTime clientModified;
    private LocalDateTime serverModified;
    private String rev;
    private long size;
    private String pathLower;
    private String pathDisplay;
    private boolean isDownloadable;
    private String contentHash;

    private FileMetadata(String name, String id, LocalDateTime clientModified, LocalDateTime serverModified, String rev, long size, String pathLower, String pathDisplay, boolean isDownloadable, String contentHash) {
        this.name = name;
        this.id = id;
        this.clientModified = clientModified;
        this.serverModified = serverModified;
        this.rev = rev;
        this.size = size;
        this.pathLower = pathLower;
        this.pathDisplay = pathDisplay;
        this.isDownloadable = isDownloadable;
        this.contentHash = contentHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getClientModified() {
        return clientModified;
    }

    public void setClientModified(LocalDateTime clientModified) {
        this.clientModified = clientModified;
    }

    public LocalDateTime getServerModified() {
        return serverModified;
    }

    public void setServerModified(LocalDateTime serverModified) {
        this.serverModified = serverModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    public boolean isDownloadable() {
        return isDownloadable;
    }

    public void setDownloadable(boolean downloadable) {
        isDownloadable = downloadable;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public FileMetadata name(String name) {
        this.name = name;
        return this;
    }

    public FileMetadata clientModified(LocalDateTime clientModified) {
        this.clientModified = clientModified;
        return this;
    }

    public FileMetadata serverModified(LocalDateTime serverModified) {
        this.serverModified = serverModified;
        return this;
    }

    public FileMetadata size(long size) {
        this.size = size;
        return this;
    }

    public FileMetadata pathLower(String pathLower) {
        this.pathLower = pathLower;
        return this;
    }

    public FileMetadata pathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
        return this;
    }

    public FileMetadata isDownloadable(boolean isDownloadable) {
        this.isDownloadable = isDownloadable;
        return this;
    }

    public FileMetadata contentHash(String contentHash) {
        this.contentHash = contentHash;
        return this;
    }

    public FileMetadata createFileMetadata() {
        return new Builder().name(name).clientModified(clientModified).serverModified(serverModified).size(size).pathLower(pathLower).pathDisplay(pathDisplay).isDownloadable(isDownloadable).contentHash(contentHash).createFileMetadata();
    }

    public static class Builder {

        private String name;
        private String id;
        private LocalDateTime clientModified;
        private LocalDateTime serverModified;
        private String rev;
        private long size;
        private String pathLower;
        private String pathDisplay;
        private boolean isDownloadable;
        private String contentHash;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder clientModified(LocalDateTime clientModified) {
            this.clientModified = clientModified;
            return this;
        }

        public Builder serverModified(LocalDateTime serverModified) {
            this.serverModified = serverModified;
            return this;
        }

        public Builder rev(String rev) {
            this.rev = rev;
            return this;
        }
        public Builder size(long size) {
            this.size = size;
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

        public Builder isDownloadable(boolean isDownloadable) {
            this.isDownloadable = isDownloadable;
            return this;
        }

        public Builder contentHash(String contentHash) {
            this.contentHash = contentHash;
            return this;
        }

        public FileMetadata createFileMetadata() {
            return new FileMetadata(name, id, clientModified, serverModified, rev, size, pathLower, pathDisplay, isDownloadable, contentHash);
        }
    }
}
