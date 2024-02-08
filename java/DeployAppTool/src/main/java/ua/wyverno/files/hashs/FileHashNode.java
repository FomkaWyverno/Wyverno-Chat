package ua.wyverno.files.hashs;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.files.IFileNode;
//import ua.wyverno.json.jackson.deserializer.FileHashDeserializer;
import ua.wyverno.json.jackson.serializer.FileHashSerializer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class just have Path File and its content hash
 */
@JsonSerialize(using = FileHashSerializer.class)
//@JsonDeserialize(using = FileHashDeserializer.class)
public class FileHashNode implements IFileNode<FileHashNode>, Hashing {

    private static final Logger logger = LoggerFactory.getLogger(FileHashNode.class);
    private String hash;
    private final String name;
    private final FileHashNode parent;
    private final List<FileHashNode> children;
    private Path path;
    private final boolean isFile;
    public FileHashNode(FileHashNode parent, String nameFile, boolean isFile) {
        this.parent = parent;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.hash = "";
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileHashNode(FileHashNode parent, String nameFile, boolean isFile, String hash) {
        this.parent = parent;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.hash = hash;
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileHashNode(String nameFile, boolean isFile) {
        this.parent = null;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.hash = "";
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileHashNode(String nameFile, boolean isFile, String hash) {
        this.parent = null;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.hash = hash;
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    @Override
    public String getHash() {
        return hash;
    }
    @Override
    public boolean isDirectory() {
        return !this.isFile;
    }

    @Override
    public boolean isFile() {
        return this.isFile;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        if (this.getParent() != null) {
            return this.getParent().getPath() + "/" + this.getName();
        }
        return this.getName();
    }

    @Override
    public Path toPath() {
        if (this.path == null) this.path = Paths.get(this.getPath());
        return this.path;
    }

    @Override
    public FileHashNode getParent() {
        return this.parent;
    }

    @Override
    public List<FileHashNode> getChildren() {
        if (this.children == null) return null;
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public void addChild(FileHashNode file) {
        this.children.add(file);
    }
    @Override
    public void addChildren(List<FileHashNode> files) {
        this.children.addAll(files);
    }

    @Override
    public boolean removeChild(FileHashNode file) {
        return this.children.remove(file);
    }

    @Override
    public boolean removeChildren(List<FileHashNode> children) {
        return this.children.removeAll(children);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileHashNode otherFile)) return false;
        if (this.isFile() && otherFile.isFile()) {
            return this.hash.equals(otherFile.getHash())
                    &&
                    this.toPath().equals(otherFile.toPath());
        }
        if (this.isDirectory() && otherFile.isDirectory()) {
            return this.toPath().equals(otherFile.toPath());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(
                """
                        {"FileHash": {
                            "hash": "%s",
                            "name": "%s",
                            "path": "%s",
                            "children": "%s"
                        }}""",
                this.getHash(),
                this.getName(),
                this.getPath(),
                this.getChildren() != null ? this.getChildren().toString() : "null");
    }
}
