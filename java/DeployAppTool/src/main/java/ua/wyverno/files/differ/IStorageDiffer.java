package ua.wyverno.files.differ;

import java.nio.file.Path;
import java.util.Set;

public interface IStorageDiffer {
    Set<Path> getAddedFiles();
    Set<Path> getModifyFiles();
    Set<Path> getDeletedFiles();
    Set<Path> getAddedFolders();
    Set<Path> getDeletedFolders();
}
