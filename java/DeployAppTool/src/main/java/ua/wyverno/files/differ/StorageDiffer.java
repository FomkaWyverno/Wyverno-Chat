package ua.wyverno.files.differ;

import ua.wyverno.files.hashs.FileHashNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
        this.findAddedFoldersAndFiles();
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
        this.findDeletedFoldersAndFiles();
        return this.deletedFiles;
    }
    @Override
    public Set<Path> getAddedFolders() {
        if (this.addedFolders != null) return this.addedFolders;
        this.findAddedFoldersAndFiles();
        return this.addedFolders;
    }
    @Override
    public Set<Path> getDeletedFolders() {
        if (this.deletedFolders != null) return this.deletedFolders;
        this.findDeletedFoldersAndFiles();
        return this.deletedFolders;
    }

    private void findDeletedFoldersAndFiles() {
        this.deletedFolders = new HashSet<>();
        this.deletedFiles = new HashSet<>();
        this.recursiveFindDeletedFoldersAndFiles(this.firstStorage, this.secondStorage);
        this.deletedFolders = Collections.unmodifiableSet(this.deletedFolders);
        this.deletedFiles = Collections.unmodifiableSet(this.deletedFiles);
    }

    /**
     * Using the recursion method finding deleted directories
     * @param firstFolder Shows what the folder should look like
     * @param secondFolder Search for deleted folders inside the folder
     */
    private void recursiveFindDeletedFoldersAndFiles(FileHashNode firstFolder, FileHashNode secondFolder) {
        if (firstFolder.isFile() || secondFolder.isFile()) throw
                new IllegalArgumentException(
                        "Argument firstFolder or secondFolder is File. firstFolder isFile() -> "
                        + firstFolder.isFile() + ", secondFolder isFile() -> " + secondFolder.isFile());

        secondFolder.getChildren()
                .forEach(secondChild -> {
                    if (secondChild.isDirectory()) {
                        this.findDeleteFolderAndAddToSet(firstFolder, secondChild);
                    } else {
                        this.findDeleteFilesAndAddToSet(firstFolder.getChildren().stream()
                                .filter(FileHashNode::isFile).toList(), secondChild);
                    }
                });
    }

    private void findDeleteFolderAndAddToSet(FileHashNode firstFolder, FileHashNode secondFolder) {
        Optional<FileHashNode> optionalFirstFolderChild = firstFolder.getChildren()
                .stream()
                .filter(FileHashNode::isDirectory)
                .filter(firstFolderChild -> firstFolderChild.getName().equals(secondFolder.getName()))
                .findFirst();
        if (optionalFirstFolderChild.isPresent()) {
            FileHashNode firstFolderChild = optionalFirstFolderChild.get();
            this.recursiveFindDeletedFoldersAndFiles(firstFolderChild, secondFolder);
        } else {
            this.deletedFolders.add(Paths.get(secondFolder.getPath()));
        }
    }

    private void findDeleteFilesAndAddToSet(List<FileHashNode> firstFiles, FileHashNode secondFile) {
        boolean isDeleteFile = firstFiles.stream()
                .noneMatch(firstFile -> firstFile.getName().equals(secondFile.getName()));
        if (isDeleteFile) {
            this.deletedFiles.add(Paths.get(secondFile.getPath()));
        }
    }

    private void findAddedFoldersAndFiles() {
        this.addedFolders = new HashSet<>();
        this.addedFiles = new HashSet<>();
        this.recursiveFindAddedFoldersAndFiles(this.firstStorage, this.secondStorage);
        this.addedFolders = Collections.unmodifiableSet(this.addedFolders);
        this.addedFiles = Collections.unmodifiableSet(this.addedFiles);
    }

    private void recursiveFindAddedFoldersAndFiles(FileHashNode firstFolder, FileHashNode secondFolder) {
        if (firstFolder.isFile() || secondFolder.isFile()) throw
                new IllegalArgumentException(
                        "Argument firstFolder or secondFolder is File. firstFolder isFile() -> "
                                + firstFolder.isFile() + ", secondFolder isFile() -> " + secondFolder.isFile());
    }
}
