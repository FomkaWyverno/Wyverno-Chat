package ua.wyverno.files.cloud;

import ua.wyverno.files.hashs.FileMetadataNode;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class SyncCloudStorageBuilder {
    private List<FileMetadataNode> applicationAbsolutePathFiles;
    private List<FileMetadataNode> applicationRelativizedPathFiles;
    private Set<Path> applicationFoldersRelativized;


    private List<FileMetadataNode> cloudFiles;
    private Set<Path> cloudFolders;

    public SyncCloudStorageBuilder applicationAbsolutePathFiles(List<FileMetadataNode> applicationAbsolutePathFiles) {
        this.applicationAbsolutePathFiles = applicationAbsolutePathFiles;
        return this;
    }

    public SyncCloudStorageBuilder applicationRelativizedPathFiles(List<FileMetadataNode> applicationRelativizedPathFiles) {
        this.applicationRelativizedPathFiles = applicationRelativizedPathFiles;
        return this;
    }

    public SyncCloudStorageBuilder applicationFoldersRelativized(Set<Path> applicationFoldersRelativized) {
        this.applicationFoldersRelativized = applicationFoldersRelativized;
        return this;
    }

    public SyncCloudStorageBuilder cloudFiles(List<FileMetadataNode> cloudFiles) {
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
