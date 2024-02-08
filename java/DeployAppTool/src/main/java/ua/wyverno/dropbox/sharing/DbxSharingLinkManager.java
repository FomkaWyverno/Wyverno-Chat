package ua.wyverno.dropbox.sharing;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.metadata.FileMetadata;
import ua.wyverno.dropbox.metadata.FolderMetadata;
import ua.wyverno.dropbox.metadata.MetadataContainer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DbxSharingLinkManager {

    private static final Logger logger = LoggerFactory.getLogger(DbxSharingLinkManager.class);

    private final DropBoxAPI api;
    private Set<SharedLinkMetadata> shareLinks;
    public DbxSharingLinkManager(DropBoxAPI api) {
        this.api = api;
    }

    /**
     * Collect share link for all content in DropBox to Set&lt;SharedLinkMetadata&gt; and save to field shareLinks
     */
    private void collectAllShareLinks() {
        try {
            MetadataContainer contentMetadata = this.api.collectAllContentFromPathAsMetadataContainer("");
            Set<String> contentPaths = Stream
                    .concat(contentMetadata.getFileMetadataList().stream().map(FileMetadata::getPathLower),
                            contentMetadata.getFolderMetadataList().stream().map(FolderMetadata::getPathLower))
                    .collect(Collectors.toSet());
            Set<SharedLinkMetadata> existSharedLinks = this.api.listSharedLinks()
                    .stream()
                    .filter(shareLink -> contentPaths.stream()
                            .anyMatch(filePath -> shareLink.getPathLower().equals(filePath)))
                    .collect(Collectors.toSet());

            Set<String> pathWithoutShareLink = contentPaths.stream()
                    .filter(path -> existSharedLinks
                            .stream().noneMatch(existShareLink -> existShareLink.getPathLower().equals(path)))
                    .collect(Collectors.toSet());
            Set<SharedLinkMetadata> shareLinks = new HashSet<>();
            if (!pathWithoutShareLink.isEmpty()) {
                StringBuilder log = new StringBuilder("Path without share link, and \n");
                shareLinks = pathWithoutShareLink.stream()
                        .peek(path -> log.append("Path: ").append(path).append("\n"))
                        .map(path -> {
                            try {
                                return this.api.createSharedLink(path);
                            } catch (DbxException e) {
                                logger.error("Error in Stream API ->", e);
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toSet());
                logger.debug(log.toString());
            } else {
                logger.debug("Path without share link not has");
            }
            shareLinks.addAll(existSharedLinks);
            this.shareLinks = shareLinks;
        } catch (DbxException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateShareLinks() {this.collectAllShareLinks();}

    /**
     * If Set with Share Links is null it will be collect all share links for content in DropBox.
     * @return Set with Share Links for all content in DropBox
     */
    public Set<SharedLinkMetadata> getShareLinks() {
        if (this.shareLinks != null) return shareLinks;
        this.collectAllShareLinks();
        return this.shareLinks;
    }
}
