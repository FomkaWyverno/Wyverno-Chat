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
public class FileMetadataNode implements IFileNode<FileMetadataNode>, Hashing {

    private static final Logger logger = LoggerFactory.getLogger(FileMetadataNode.class);
    private final String contentHash;
    private final String name;
    private final FileMetadataNode parent;
    private final List<FileMetadataNode> children;
    private Path path;
    private final boolean isFile;
    public FileMetadataNode(FileMetadataNode parent, String nameFile, boolean isFile) {
        this.parent = parent;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.contentHash = "";
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileMetadataNode(FileMetadataNode parent, String nameFile, boolean isFile, String contentHash) {
        this.parent = parent;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.contentHash = contentHash;
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileMetadataNode(String nameFile, boolean isFile) {
        this.parent = null;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.contentHash = "";
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    public FileMetadataNode(String nameFile, boolean isFile, String contentHash) {
        this.parent = null;
        this.name = Objects.requireNonNull(nameFile);
        this.isFile = isFile;
        this.contentHash = contentHash;
        this.children = !this.isFile ? new ArrayList<>() : null;
    }
    @Override
    public String getContentHash() {
        return contentHash;
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
    public FileMetadataNode getParent() {
        return this.parent;
    }

    @Override
    public List<FileMetadataNode> getChildren() {
        if (this.children == null) return null;
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public void addChild(FileMetadataNode file) {
        this.children.add(file);
    }
    @Override
    public void addChildren(List<FileMetadataNode> files) {
        this.children.addAll(files);
    }

    @Override
    public boolean removeChild(FileMetadataNode file) {
        return this.children.remove(file);
    }

    @Override
    public boolean removeChildren(List<FileMetadataNode> children) {
        return this.children.removeAll(children);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileMetadataNode otherFile)) return false;
        if (this.isFile() && otherFile.isFile()) {
            return this.contentHash.equals(otherFile.getContentHash())
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
                this.getContentHash(),
                this.getName(),
                this.getPath(),
                this.getChildren() != null ? this.getChildren().toString() : "null");
    }
}
