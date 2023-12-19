package ua.wyverno.files;

import ua.wyverno.files.hashs.FileHashInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SyncCloudStorage {

    private final List<FileHashInfo> applicationFiles;
    private final List<FileHashInfo> cloudFiles;

    private Set<Path> applicationFolders;
    private Set<Path> cloudFolders;

    private Set<Path> deletedFolders;
    private Set<FileHashInfo> deletedFiles;
    private Set<FileHashInfo> addedFiles;
    private Set<FileHashInfo> updatedFiles;

    public SyncCloudStorage(List<FileHashInfo> applicationFiles, List<FileHashInfo> cloudFiles) {
        this.applicationFiles = applicationFiles;
        this.cloudFiles = cloudFiles;
    }

    public Set<FileHashInfo> getDeletedFiles() {
        if (this.deletedFiles != null) return this.deletedFiles;
        this.deletedFiles = this.cloudFiles
                .stream()
                .filter(file -> !this.applicationFiles.contains(file))
                .collect(Collectors.toSet());

        return this.deletedFiles;
    }

    public Set<FileHashInfo> getAddedFiles() {
        if (this.addedFiles != null) return this.addedFiles;

        this.addedFiles = this.applicationFiles
                .stream()
                .filter(file -> !this.cloudFiles.contains(file))
                .collect(Collectors.toSet());

        return this.addedFiles;
    }

    /* TODO: Method not realize.
    * TODO: METHOD Must do collect deleted (If need deleted core\java, so not need deleted core\java\bin - for optimization) folders from CloudFiles comparing them with ApplicationFiles
    * TODO: Method must do return Set<Path> with size 1 if need remove root file CloudFiles and this once Path must be "./"
    * */
    public Set<Path> getDeletedFolders() {
        if (this.deletedFolders != null) return this.deletedFolders;

        return this.deletedFolders;
    }

    public Set<Path> getApplicationFolders() {
        if (this.applicationFolders != null) return applicationFolders;

        this.applicationFolders = this.applicationFiles
                .stream()
                .map(file -> file.getPathFile().getParent())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return this.applicationFolders;
    }

    public Set<Path> getCloudFolders() {
        if (this.cloudFolders != null) return this.cloudFolders;

        this.cloudFolders = this.cloudFiles
                .stream()
                .map(file -> file.getPathFile().getParent())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return this.cloudFolders;
    }
}
