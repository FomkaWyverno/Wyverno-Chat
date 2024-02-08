package ua.wyverno.files;

import java.nio.file.Path;
import java.util.List;

public interface IFileNode<T extends IFileNode<T>> {
    boolean isDirectory();
    boolean isFile();
    String getName();
    String getPath();
    Path toPath();
    T getParent();
    List<T> getChildren();
    void addChild(T file);
    void addChildren(List<T> files);
    boolean removeChild(T File);
    boolean removeChildren(List<T> children);
}
