package ua.wyverno.dropbox.modules;

import com.dropbox.core.DbxException;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.files.hashs.CloudFileNodeHash;

import java.io.IOException;
import java.util.List;

public interface IFilesAPI {
    void deleteFile(String folderPath) throws DbxException;
    void deleteFiles(List<String> foldersPath) throws DbxException;
    void createFolder(String folderPath) throws DbxException;
    void createFolders(List<String> foldersUpload) throws DbxException;
    void uploadFile(CloudLocalFile fileUpload) throws DbxException, IOException;
    void uploadFiles(List<CloudLocalFile> filesUpload) throws DbxException, IOException;
    MetadataContainer getListFolderAsMetadataContainer(String path) throws DbxException;
    MetadataContainer collectAllContentFromPathAsMetadataContainer(String path) throws DbxException;
    CloudFileNodeHash collectRootContentAsCloudFileHashNode() throws DbxException;
}
