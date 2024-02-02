package ua.wyverno.dropbox.modules;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.ListSharedLinksBuilder;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;

import java.util.List;

public interface ISharingAPI {
    List<SharedLinkMetadata> listSharedLinks(ListSharedLinksBuilder listSharedLinksBuilder) throws DbxException;
    List<SharedLinkMetadata> listSharedLinks() throws DbxException;
    SharedLinkMetadata createSharedLink(String path) throws DbxException;
    SharedLinkMetadata createSharedLink(String path, SharedLinkSettings sharedLinkSettings) throws DbxException;

}
