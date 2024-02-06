package ua.wyverno.files.differ;

import ua.wyverno.files.hashs.FileHash;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageDiffer implements IStorageDiffer {

    private final Set<FileHash> firstStorage;
    private final Set<FileHash> secondStorage;


    private Set<Path> addedFiles;
    private Set<Path> modifyFiles;
    private Set<Path> deletedFiles;
    private Set<Path> addedFolders;
    private Set<Path> deletedFolders;
    public StorageDiffer(Set<FileHash> firstStorage, Set<FileHash> secondStorage) {
        this.firstStorage = Collections.unmodifiableSet(firstStorage);
        this.secondStorage = Collections.unmodifiableSet(secondStorage);
    }

    @Override
    public Set<Path> getAddedFiles() {
        if (this.addedFiles != null) return this.addedFiles;
        this.addedFiles = new HashSet<>();
        return this.addedFiles;
    }
    @Override
    public Set<Path> getModifyFiles() {
        if (this.modifyFiles != null) return this.modifyFiles;
        this.modifyFiles = new HashSet<>();
        return this.modifyFiles;
    }
    @Override
    public Set<Path> getDeletedFiles() {
        if (this.deletedFiles != null) return this.deletedFiles;
        this.deletedFiles = new HashSet<>();
        return this.deletedFiles;
    }
    @Override
    public Set<Path> getAddedFolders() {
        if (this.addedFolders != null) return this.addedFolders;
        this.addedFolders = new HashSet<>();
        return this.addedFolders;
    }
    @Override
    public Set<Path> getDeletedFolders() {
        if (this.deletedFolders != null) return this.deletedFolders;
        Set<Path> deletedDirectories = new HashSet<>();
        Set<FileHash> firstStorageDirectories = this.firstStorage.stream()
                .filter(FileHash::isDirectory)
                .collect(Collectors.toSet());
        Set<FileHash> secondStorageDirectories = this.secondStorage.stream()
                .filter(FileHash::isDirectory)
                .collect(Collectors.toSet());



        this.deletedFolders = Collections.unmodifiableSet(deletedDirectories);
        return this.deletedFolders;
    }
}
