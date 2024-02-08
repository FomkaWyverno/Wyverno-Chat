package ua.wyverno.files.hashs;

import ua.wyverno.files.ICloudFile;

public class CloudFileNodeHash extends FileHashNode implements ICloudFile {

    private final boolean isCloudFile;

    public CloudFileNodeHash(FileHashNode parent, String nameFile, boolean isCloudFile) {
        super(parent, nameFile, isCloudFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileNodeHash(String nameFile, boolean isCloudFile) {
        super(nameFile, isCloudFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileNodeHash(FileHashNode parent, String nameFile, String hash, boolean isCloudFile) {
        super(parent, nameFile, isCloudFile, hash);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileNodeHash(String nameFile, String hash, boolean isCloudFile) {
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
