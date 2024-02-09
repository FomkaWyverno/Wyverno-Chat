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
import ua.wyverno.dropbox.modules.files.DropBoxFilesApi;
import ua.wyverno.dropbox.modules.DropBoxSharingAPI;
import ua.wyverno.dropbox.modules.IFilesAPI;
import ua.wyverno.dropbox.modules.ISharingAPI;
import ua.wyverno.files.hashs.CloudFileNodeHash;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ua.wyverno.Main.CONFIG;

public class DropBoxAPI implements IFilesAPI, ISharingAPI {

    private static final Logger logger = LoggerFactory.getLogger(DropBoxAPI.class);
    private static final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/client").withAutoRetryEnabled().build();

    private DbxClientV2 dbxClientV2;
    private DropBoxFilesApi filesApi;
    private DropBoxSharingAPI sharingAPI;

    public DropBoxAPI(String accessToken) {
        try {
            this.dbxClientV2 = new DbxClientV2(config, Objects.requireNonNullElse(accessToken, ""));
            if (this.isNotValidAccessToken()) {
                logger.debug("Access Token is not valid or not present!");
                this.updateAccessTokenApi();
            }
            this.filesApi = new DropBoxFilesApi(this.dbxClientV2.files());
            this.sharingAPI = new DropBoxSharingAPI(this.dbxClientV2.sharing());
        } catch (DbxException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void updateAccessTokenApi() throws DbxException, IOException {
        logger.debug("DropBox Access Token Updating...");
        CONFIG.updateDbxAccessToken();
        String accessTokenDbX = CONFIG.getAccessTokenDropBox();
        this.dbxClientV2 = new DbxClientV2(config, accessTokenDbX);
    }

    private void accessTokenHasExpired() throws DbxException {
        try {
            logger.warn("DropBox Access Token has expired!");
            this.updateAccessTokenApi();
            this.filesApi = new DropBoxFilesApi(this.dbxClientV2.files());
            this.sharingAPI = new DropBoxSharingAPI(this.dbxClientV2.sharing());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isNotValidAccessToken() throws DbxException {
        try {
            this.dbxClientV2.check().user().getResult();
            return false;
        } catch (InvalidAccessTokenException | BadRequestException e) {
            return true;
        }
    }

    @Override
    public void deleteFile(String folderPath) throws DbxException {
        try {
            this.filesApi.deleteFile(folderPath);
        } catch (InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public void deleteFiles(List<String> foldersPath) throws DbxException {
        try {
            this.filesApi.deleteFiles(foldersPath);
        } catch (InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public void createFolder(String folderPath) throws DbxException {
        try {
            this.filesApi.createFolder(folderPath);
        } catch (InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public void createFolders(List<String> foldersUpload) throws DbxException {
        try {
            this.filesApi.createFolders(foldersUpload);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public void uploadFile(CloudLocalFile fileUpload) throws DbxException, IOException {
        try {
            this.filesApi.uploadFile(fileUpload);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public void uploadFiles(List<CloudLocalFile> filesUpload) throws DbxException, IOException {
        try {
            this.filesApi.uploadFiles(filesUpload);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
        }
    }

    @Override
    public MetadataContainer getListFolderAsMetadataContainer(String path) throws DbxException {
        try {
            return this.filesApi.getListFolderAsMetadataContainer(path);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.filesApi.getListFolderAsMetadataContainer(path);
        }
    }

    @Override
    public MetadataContainer collectAllContentFromPathAsMetadataContainer(String path) throws DbxException {
        try {
            return this.filesApi.collectAllContentFromPathAsMetadataContainer(path);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.filesApi.collectAllContentFromPathAsMetadataContainer(path);
        }
    }

    @Override
    public CloudFileNodeHash collectRootContentAsCloudFileHashNode() throws DbxException {
        try {
            return this.filesApi.collectRootContentAsCloudFileHashNode();
        } catch (InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.filesApi.collectRootContentAsCloudFileHashNode();
        }
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks(ListSharedLinksBuilder listSharedLinksBuilder) throws DbxException {
        try {
            return this.sharingAPI.listSharedLinks(listSharedLinksBuilder);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.sharingAPI.listSharedLinks(listSharedLinksBuilder);
        }
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks() throws DbxException {
        try {
            return this.sharingAPI.listSharedLinks();
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.sharingAPI.listSharedLinks();
        }
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path) throws DbxException {
        try {
            return this.sharingAPI.createSharedLink(path);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.sharingAPI.createSharedLink(path);
        }
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path, SharedLinkSettings sharedLinkSettings) throws DbxException {
        try {
            return this.sharingAPI.createSharedLink(path, sharedLinkSettings);
        } catch(InvalidAccessTokenException e) {
            this.accessTokenHasExpired();
            return this.sharingAPI.createSharedLink(path, sharedLinkSettings);
        }
    }
}
