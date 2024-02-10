package ua.wyverno.files.differ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.hashs.FileMetadataNode;

import java.nio.file.Path;
import java.util.*;

public class StorageDiffer implements IStorageDiffer {
    private static final Logger logger = LoggerFactory.getLogger(StorageDiffer.class);
    private final FileMetadataNode firstStorage;
    private final FileMetadataNode secondStorage;


    private Set<Path> addedFiles;
    private Set<Path> modifyFiles;
    private Set<Path> deletedFiles;
    private Set<Path> addedFolders;
    private Set<Path> deletedFolders;

    public StorageDiffer(FileMetadataNode firstStorage, FileMetadataNode secondStorage) {
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
        this.findDeletedOrModifyFoldersAndFiles();
        return this.modifyFiles;
    }

    @Override
    public Set<Path> getDeletedFiles() {
        if (this.deletedFiles != null) return this.deletedFiles;
        this.findDeletedOrModifyFoldersAndFiles();
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
        this.findDeletedOrModifyFoldersAndFiles();
        return this.deletedFolders;
    }

    private void findDeletedOrModifyFoldersAndFiles() {
        this.deletedFolders = new HashSet<>();
        this.deletedFiles = new HashSet<>();
        this.modifyFiles = new HashSet<>();
        this.recursiveFindDeletedOrModifyFoldersAndFiles(this.firstStorage, this.secondStorage);
        this.deletedFolders = Collections.unmodifiableSet(this.deletedFolders);
        this.deletedFiles = Collections.unmodifiableSet(this.deletedFiles);
        this.modifyFiles = Collections.unmodifiableSet(this.modifyFiles);
    }

    /**
     * Using the recursion method finding deleted directories or files
     * Also checks if the file has been modified and adds it to the Set
     * @param firstFolder  Shows what the folder should look like
     * @param secondFolder Search for deleted folders inside the folder
     */
    private void recursiveFindDeletedOrModifyFoldersAndFiles(FileMetadataNode firstFolder, FileMetadataNode secondFolder) {
        if (firstFolder.isFile() || secondFolder.isFile()) throw
                new IllegalArgumentException(
                        "Argument firstFolder or secondFolder is File. firstFolder isFile() -> "
                                + firstFolder.isFile() + ", secondFolder isFile() -> " + secondFolder.isFile());

        secondFolder.getChildren()
                .forEach(secondChild -> {
                    if (secondChild.isDirectory()) {
                        this.findDeleteFolderAndAddToSet(firstFolder, secondChild);
                    } else {
                        this.findDeleteFilesOrModifyAndAddToSet(firstFolder.getChildren().stream()
                                .filter(FileMetadataNode::isFile).toList(), secondChild);
                    }
                });
    }

    private void findDeleteFolderAndAddToSet(FileMetadataNode firstFolder, FileMetadataNode secondChildFolder) {
        firstFolder.getChildren()
                .stream()
                .filter(FileMetadataNode::isDirectory)
                .filter(firstFolderChild -> firstFolderChild.getName().equals(secondChildFolder.getName()))
                .findAny()
                .ifPresentOrElse(
                        firstFolderChild -> this.recursiveFindDeletedOrModifyFoldersAndFiles(firstFolderChild, secondChildFolder), // If folder in second storage is exists
                        () -> this.deletedFolders.add(secondChildFolder.toPath())); // If the folder was deleted
    }

    private void findDeleteFilesOrModifyAndAddToSet(List<FileMetadataNode> firstFiles, FileMetadataNode secondFile) {
        firstFiles.stream()
                .filter(firstFile -> firstFile.getName().equals(secondFile.getName()))
                .findAny()
                .ifPresentOrElse(
                        firstFile -> { // If file in folder is exists
                            if (!firstFile.getContentHash().equals(secondFile.getContentHash())) {
                                this.modifyFiles.add(firstFile.toPath()); // If file was modify
                            }
                        },
                        () -> this.deletedFiles.add(secondFile.toPath())); // If file was deleted
    }

    private void findAddedFoldersAndFiles() {
        this.addedFolders = new HashSet<>();
        this.addedFiles = new HashSet<>();
        this.recursiveFindAddedFoldersAndFiles(this.firstStorage, this.secondStorage);
        this.addedFolders = Collections.unmodifiableSet(this.addedFolders);
        this.addedFiles = Collections.unmodifiableSet(this.addedFiles);
    }

    private void recursiveFindAddedFoldersAndFiles(FileMetadataNode firstFolder, FileMetadataNode secondFolder) {
        if (firstFolder.isFile() || secondFolder.isFile()) throw
                new IllegalArgumentException(
                        "Argument firstFolder or secondFolder is File. firstFolder isFile() -> "
                                + firstFolder.isFile() + ", secondFolder isFile() -> " + secondFolder.isFile());
        firstFolder.getChildren()
                .forEach(firstChild -> {
                    if (firstChild.isDirectory()) {
                        secondFolder.getChildren().stream() // Check for new directories
                                .filter(FileMetadataNode::isDirectory)
                                .filter(secondChild -> firstChild.getName().equals(secondChild.getName()))
                                .findAny()
                                .ifPresentOrElse(presentSecondChild -> // if this folder is exists
                                                this.recursiveFindAddedFoldersAndFiles(firstChild, presentSecondChild),
                                        () -> this.recursiveAddDirectory(firstChild));

                    } else {
                        boolean isNewFile = secondFolder.getChildren().stream()
                                .filter(FileMetadataNode::isFile)
                                .noneMatch(secondChild -> secondChild.getName().equals(firstChild.getName()));
                        if (isNewFile) {
                            this.addedFiles.add(firstChild.toPath());
                        }
                    }
                });
    }

    /**
     * Recursively adds all the contents of a directory to collections
     *
     * @param newDirectory The directory to be added
     */
    private void recursiveAddDirectory(FileMetadataNode newDirectory) {
        if (newDirectory.isFile())
            throw new IllegalArgumentException("Argument newDirectory is File! newDirectory = " + newDirectory);
        boolean directoryNotHasOtherDirectory = newDirectory.getChildren().stream()
                .filter(FileMetadataNode::isDirectory)
                .findAny()
                .isEmpty();
        if (directoryNotHasOtherDirectory) {
            this.addedFolders.add(newDirectory.toPath());
        }

        newDirectory.getChildren()
                .forEach(child -> {
                    if (child.isDirectory()) {
                        this.recursiveAddDirectory(child);
                    } else {
                        this.addedFiles.add(child.toPath());
                    }
                });
    }
}
