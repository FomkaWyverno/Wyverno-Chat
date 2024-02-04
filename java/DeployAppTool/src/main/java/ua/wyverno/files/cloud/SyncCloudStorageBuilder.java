package ua.wyverno.files.cloud;

import ua.wyverno.files.hashs.FileHashInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class SyncCloudStorageBuilder {
    private List<FileHashInfo> applicationAbsolutePathFiles;
    private List<FileHashInfo> applicationRelativizedPathFiles;
    private Set<Path> applicationFoldersRelativized;


    private List<FileHashInfo> cloudFiles;
    private Set<Path> cloudFolders;

    public SyncCloudStorageBuilder applicationAbsolutePathFiles(List<FileHashInfo> applicationAbsolutePathFiles) {
        this.applicationAbsolutePathFiles = applicationAbsolutePathFiles;
        return this;
    }

    public SyncCloudStorageBuilder applicationRelativizedPathFiles(List<FileHashInfo> applicationRelativizedPathFiles) {
        this.applicationRelativizedPathFiles = applicationRelativizedPathFiles;
        return this;
    }

    public SyncCloudStorageBuilder applicationFoldersRelativized(Set<Path> applicationFoldersRelativized) {
        this.applicationFoldersRelativized = applicationFoldersRelativized;
        return this;
    }

    public SyncCloudStorageBuilder cloudFiles(List<FileHashInfo> cloudFiles) {
        this.cloudFiles = cloudFiles;
        return this;
    }

    public SyncCloudStorageBuilder cloudFolders(Set<Path> cloudFolders) {
        this.cloudFolders = cloudFolders;
        return this;
    }

    public SyncCloudStorage build() {
        return new SyncCloudStorage(
                applicationAbsolutePathFiles,
                applicationRelativizedPathFiles,
                applicationFoldersRelativized,
                cloudFiles,
                cloudFolders);
    }
}
