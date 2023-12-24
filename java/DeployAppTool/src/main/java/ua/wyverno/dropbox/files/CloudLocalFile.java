package ua.wyverno.dropbox.files;

import java.nio.file.Path;

public class CloudLocalFile {
    private Path localFile;
    private Path cloudFile;

    public CloudLocalFile(Path localFile, Path cloudFile) {
        this.localFile = localFile;
        this.cloudFile = cloudFile;
    }

    public Path getLocalFile() {
        return localFile;
    }

    public Path getCloudFile() {
        return cloudFile;
    }
}
