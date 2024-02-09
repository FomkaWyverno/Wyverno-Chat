package ua.wyverno.files.differ;

import ua.wyverno.files.hashs.FileHashNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageDiffer implements IStorageDiffer {

    private final FileHashNode firstStorage;
    private final FileHashNode secondStorage;


    private Set<Path> addedFiles;
    private Set<Path> modifyFiles;
    private Set<Path> deletedFiles;
    private Set<Path> addedFolders;
    private Set<Path> deletedFolders;
    public StorageDiffer(FileHashNode firstStorage, FileHashNode secondStorage) {
        this.firstStorage = firstStorage;
        this.secondStorage = secondStorage;
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

        this.recursiveFindDeletedFolders(deletedDirectories, this.firstStorage, this.secondStorage);
        this.deletedFolders = Collections.unmodifiableSet(deletedDirectories);
        return this.deletedFolders;
    }

    /**
     * Using the recursion method finding deleted directories
     * @param deletedFolders Set where should append deleted directories
     * @param firstFolder Shows what the folder should look like
     * @param secondFolder Search for deleted folders inside the folder
     */
    private void recursiveFindDeletedFolders(Set<Path> deletedFolders, FileHashNode firstFolder, FileHashNode secondFolder) {
        if (firstFolder.isFile() || secondFolder.isFile()) throw
                new IllegalArgumentException(
                        "Argument firstFolder or secondFolder is File. firstFolder isFile() -> "
                        + firstFolder.isFile() + ", secondFolder isFile() -> " + secondFolder.isFile());
        if (deletedFolders == null) throw new NullPointerException("Argument deletedFolders is null!");


        secondFolder.getChildren().stream()
                .filter(FileHashNode::isDirectory)
                .forEach(secondFolderChild -> {
                    Optional<FileHashNode> optionalFirstFolderChild = firstFolder.getChildren()
                            .stream()
                            .filter(FileHashNode::isDirectory)
                            .filter(firstFolderChild -> firstFolderChild.getName().equals(secondFolderChild.getName()))
                            .findFirst();
                    if (optionalFirstFolderChild.isPresent()) {
                        FileHashNode firstFolderChild = optionalFirstFolderChild.get();
                        this.recursiveFindDeletedFolders(deletedFolders, firstFolderChild, secondFolderChild);
                    } else {
                        deletedFolders.add(Paths.get(secondFolderChild.getPath()));

                    }
                });
    }
}
