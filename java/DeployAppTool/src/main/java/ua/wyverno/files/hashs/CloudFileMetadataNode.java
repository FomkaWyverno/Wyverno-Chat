package ua.wyverno.files.hashs;

import ua.wyverno.files.ICloudFile;

public class CloudFileMetadataNode extends FileMetadataNode implements ICloudFile {

    private final boolean isCloudFile;

    public CloudFileMetadataNode(FileMetadataNode parent, String nameFile, boolean isCloudFile) {
        super(parent, nameFile, isCloudFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileMetadataNode(String nameFile, boolean isCloudFile) {
        super(nameFile, isCloudFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileMetadataNode(FileMetadataNode parent, String nameFile, String hash, boolean isCloudFile) {
        super(parent, nameFile, isCloudFile, hash);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileMetadataNode(String nameFile, String hash, boolean isCloudFile) {
        super(nameFile, isCloudFile, hash);
        this.isCloudFile = isCloudFile;
    }
    @Override
    public boolean isDirectory() {
        return !this.isCloudFile;
    }

    @Override
    public boolean isFile() {
        return this.isCloudFile;
    }

    @Override
    public boolean isCloudFile() {
        return this.isCloudFile;
    }

    @Override
    public boolean isCloudDirectory() {
        return !this.isCloudFile;
    }
}
