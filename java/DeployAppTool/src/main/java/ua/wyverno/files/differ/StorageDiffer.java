package ua.wyverno.files.differ;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class StorageDiffer implements IStorageDiffer {

    private final Set<File> firstStorage;
    private final Set<File> secondStorage;

    public StorageDiffer(Set<File> firstStorage, Set<File> secondStorage) {
        this.firstStorage = Collections.unmodifiableSet(firstStorage);
        this.secondStorage = Collections.unmodifiableSet(secondStorage);
    }

    @Override
    public Set<Path> getAddedFiles() {
        return null;
    }

    @Override
    public Set<Path> getModifyFiles() {
        return null;
    }

    @Override
    public Set<Path> getDeletedFiles() {
        return null;
    }

    @Override
    public Set<Path> getAddedFolders() {
        return null;
    }

    @Override
    public Set<Path> getDeletedFolders() {
        return null;
    }
}
