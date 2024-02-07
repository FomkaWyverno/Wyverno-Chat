package ua.wyverno.files.hashs;

import ua.wyverno.files.ICloudFile;
import java.nio.file.Path;

public class CloudFileHash extends FileHash implements ICloudFile {

    private final boolean isCloudFile;

    public CloudFileHash(FileHash parent, String nameFile, boolean isFile, boolean isCloudFile) {
        super(parent, nameFile, isFile);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileHash(String nameFile, boolean isFile, boolean isCloudFile) {
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
