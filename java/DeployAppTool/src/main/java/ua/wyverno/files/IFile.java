package ua.wyverno.files;

import java.nio.file.Path;
import java.util.List;

public interface IFile<T extends IFile<T>> {
    boolean isDirectory();
    boolean isFile();
    Path getRelativePath();
    Path getAbsolutePath();
    T getParent();
    List<T> getChildren();
}
