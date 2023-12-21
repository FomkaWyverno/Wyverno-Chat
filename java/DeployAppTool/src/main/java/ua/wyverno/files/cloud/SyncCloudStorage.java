package ua.wyverno.files.cloud;

import ua.wyverno.files.hashs.FileHashInfo;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
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
        this.deletedFiles = this.getCloudFiles()
                .stream()
                .filter(file ->
                        this.getDeletedFolders()
                                .stream()
                                .noneMatch(deletedFolder -> file.getPathFile().startsWith(deletedFolder)))
                .filter(file -> !this.getApplicationFiles().contains(file))
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

    public Set<Path> getDeletedFolders() {
        if (this.deletedFolders != null) return this.deletedFolders;

        Set<Path> checkedFolders = new HashSet<>();

        this.deletedFolders = new HashSet<>();
        this.getCloudFolders()
                .stream()
                .filter(cloudFolder -> this.deletedFolders.stream().noneMatch(cloudFolder::startsWith))
                .forEach(cloudFolder -> {
                    Path notCheckedRoot = cloudFolder;
                    while (notCheckedRoot.getParent() != null && !this.deletedFolders.contains(notCheckedRoot.getParent())
                            &&
                            !checkedFolders.contains(notCheckedRoot.getParent())) {
                        notCheckedRoot = notCheckedRoot.getParent();
                    }

                    final Path finalNotCheckedRoot = notCheckedRoot;
                    boolean notHasFileInFolder =  this.getApplicationFiles()
                            .stream()
                            .noneMatch(file -> file.getPathFile().startsWith(finalNotCheckedRoot));

                    checkedFolders.add(finalNotCheckedRoot);
                    if (notHasFileInFolder) this.deletedFolders.add(finalNotCheckedRoot);
                });



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

    public List<FileHashInfo> getApplicationFiles() {
        return applicationFiles;
    }

    public List<FileHashInfo> getCloudFiles() {
        return cloudFiles;
    }
}
