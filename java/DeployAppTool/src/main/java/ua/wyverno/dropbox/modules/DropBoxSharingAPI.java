package ua.wyverno.dropbox.modules;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DropBoxSharingAPI implements ISharingAPI {
    private static final Logger logger = LoggerFactory.getLogger(DropBoxSharingAPI.class);

    private final DbxUserSharingRequests dbxSharing;

    public DropBoxSharingAPI(DbxUserSharingRequests dbxSharing) {
        this.dbxSharing = dbxSharing;
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks(ListSharedLinksBuilder listSharedLinksBuilder) throws DbxException {
        logger.info("Call to DropBox API Endpoint /list_shared_links");
        ListSharedLinksResult resultListLinks = listSharedLinksBuilder.start();
        List<SharedLinkMetadata> listSharedLinkMetadata = resultListLinks.getLinks();
        while (resultListLinks.getHasMore()) {
            resultListLinks = this.dbxSharing.listSharedLinksBuilder().withCursor(resultListLinks.getCursor()).start();
            listSharedLinkMetadata.addAll(resultListLinks.getLinks());
        }
        return listSharedLinkMetadata;
    }

    @Override
    public List<SharedLinkMetadata> listSharedLinks() throws DbxException {
        return this.listSharedLinks(this.dbxSharing.listSharedLinksBuilder());
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path) throws DbxException {
        logger.info("Call to DropBox API Endpoint /create_shared_link_with_settings \nParameters: \n path: {}", path);
        return this.dbxSharing.createSharedLinkWithSettings(path);
    }

    @Override
    public SharedLinkMetadata createSharedLink(String path, SharedLinkSettings sharedLinkSettings) throws DbxException {
        logger.info("Call to DropBox API Endpoint /create_shared_link_with_settings \nParameters: \n path: {},\nsettings: {}",
                path, sharedLinkSettings.toStringMultiline());
        return this.dbxSharing.createSharedLinkWithSettings(path, sharedLinkSettings);
    }
}
