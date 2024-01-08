package ua.wyverno.dropbox.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetadataContainer {
    private final List<FolderMetadata> folderMetadataList;
    private final List<FileMetadata> fileMetadataList;

    public MetadataContainer() {
        this.folderMetadataList = new ArrayList<>();
        this.fileMetadataList = new ArrayList<>();
    }

    public void addFolderMetadata(FolderMetadata folderMetadata) {
        this.folderMetadataList.add(folderMetadata);
    }

    public void addFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadataList.add(fileMetadata);
    }

    public List<FolderMetadata> getFolderMetadataList() {
        return Collections.unmodifiableList(folderMetadataList);
    }

    public List<FileMetadata> getFileMetadataList() {
        return Collections.unmodifiableList(fileMetadataList);
    }

    public void addMetadataContainer(MetadataContainer container) {
        this.folderMetadataList.addAll(container.getFolderMetadataList());
        this.fileMetadataList.addAll(container.getFileMetadataList());
    }
}
