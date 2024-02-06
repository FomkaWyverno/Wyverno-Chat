package ua.wyverno.files.hashs;

import ua.wyverno.files.ICloudFile;
import java.nio.file.Path;

public class CloudFileHash extends FileHash implements ICloudFile {

    private final boolean isCloudFile;
    public CloudFileHash(Path relativePath, Path absolutePath, boolean isCloudFile) {
        super(relativePath, absolutePath);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileHash(Path relativePath, Path absolutePath, String hash, boolean isCloudFile) {
        super(relativePath, absolutePath, hash);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileHash(String relativePath, String absolutePath, boolean isCloudFile) {
        super(relativePath, absolutePath);
        this.isCloudFile = isCloudFile;
    }

    public CloudFileHash(String relativePath, String absolutePath, String hash, boolean isCloudFile) {
        super(relativePath, absolutePath, hash);
        this.isCloudFile = isCloudFile;
    }

    @Override
    public void calculateChecksum() {
        throw new UnsupportedOperationException("calculateChecksum() method is not supported for CloudFileHash, as it does not have direct access to data");
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
