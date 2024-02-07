package ua.wyverno.files;

import java.nio.file.Path;
import java.util.List;

public interface IFile<T extends IFile<T>> {
    boolean isDirectory();
    boolean isFile();
    String getName();
    String getPath();
    Path toPath();
    T getParent();
    List<T> getChildren();
    void addChild(T file);
    void addChildren(List<T> files);
}
