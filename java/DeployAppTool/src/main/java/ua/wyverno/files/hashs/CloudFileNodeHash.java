package ua.wyverno.files.hashs;

import ua.wyverno.files.ICloudFile;

public class CloudFileNodeHash extends FileHashNode implements ICloudFile {

    private final boolean isCloudFile;

    public CloudFileNodeHash(FileHashNode parent, String nameFile, boolean isFile, boolean isCloudFile) {
        super(parent, nameFile, isFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileNodeHash(String nameFile, boolean isFile, boolean isCloudFile) {
        super(nameFile, isFile);
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
