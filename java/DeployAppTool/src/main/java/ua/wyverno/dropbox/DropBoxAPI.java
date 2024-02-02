package ua.wyverno.dropbox;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.ListSharedLinksBuilder;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.dropbox.modules.DropBoxFilesApi;
import ua.wyverno.dropbox.modules.DropBoxSharingAPI;
import ua.wyverno.dropbox.modules.IFilesAPI;
import ua.wyverno.dropbox.modules.ISharingAPI;

import java.io.IOException;
import java.util.List;

public class DropBoxAPI implements IFilesAPI, ISharingAPI {

    private static final Logger logger = LoggerFactory.getLogger(DropBoxAPI.class);
    private static final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/client").build();

    private final DbxClientV2 dbxClientV2;
    private final DropBoxFilesApi filesApi;
    private final DropBoxSharingAPI sharingAPI;

    public DropBoxAPI(String accessToken) {
        this.dbxClientV2 = new DbxClientV2(config, accessToken);
        this.filesApi = new DropBoxFilesApi(this.dbxClientV2.files());
        this.sharingAPI = new DropBoxSharingAPI(this.dbxClientV2.sharing());
    }

    public boolean isValidAccessToken() throws DbxException {
        try {
            this.dbxClientV2.check().user().getResult();
            return true;
        } catch (InvalidAccessTokenException | BadRequestException e) {
            return false;
        }
    }

    @Override
    public void deleteFile(String folderPath) throws DbxException {
        this.filesApi.deleteFile(folderPath);
    }

    @Override
    public void deleteFiles(List<String> foldersPath) throws DbxException {
        this.filesApi.deleteFiles(foldersPath);
    }

    @Override
    public void createFolder(String folderPath) throws DbxException {
        this.filesApi.createFolder(folderPath);
    }

    @Override
    public void createFolders(List<String> foldersUpload) throws DbxException {
        this.filesApi.createFolders(foldersUpload);
    }

    @Override
    public void uploadFile(CloudLocalFile fileUpload) throws DbxException, IOException {
        this.filesApi.uploadFile(fileUpload);
    }

    @Override
    public void uploadFiles(List<CloudLocalFile> filesUpload) throws DbxException, IOException {
        this.filesApi.uploadFiles(filesUpload);
    }

    @Override
    public MetadataContainer getListFolder(String path) throws DbxException {
        return this.filesApi.getListFolder(path);
    }

    @Override
    public MetadataContainer collectAllContentFromPath(String path) throws DbxException {
        return this.filesApi.collectAllContentFromPath(path);
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks(ListSharedLinksBuilder listSharedLinksBuilder) throws DbxException {
        return this.sharingAPI.listSharedLinks(listSharedLinksBuilder);
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks() throws DbxException {
        return this.sharingAPI.listSharedLinks();
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path) throws DbxException {
        return this.sharingAPI.createSharedLink(path);
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path, SharedLinkSettings sharedLinkSettings) throws DbxException {
        return this.sharingAPI.createSharedLink(path,sharedLinkSettings);
    }
}
