package ua.wyverno.files.cloud;

import com.dropbox.core.DbxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.DropBoxAPI;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.files.hashs.FileHashInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SyncCloudStorage {

    private static final Logger logger = LoggerFactory.getLogger(SyncCloudStorage.class);

    private final List<FileHashInfo> applicationFiles;
    private final List<FileHashInfo> applicationRelativizedPathFiles;
    private final List<FileHashInfo> cloudFiles;

    private final Set<Path> applicationFoldersRelativized;
    private final Set<Path> cloudFolders;

    private Set<Path> deletedFolders;
    private Set<Path> addedFolders;
    private Set<FileHashInfo> deletedFiles;
    private Set<FileHashInfo> addedOrModifyFiles;

    protected SyncCloudStorage(List<FileHashInfo> applicationAbsolutePathFiles,
                            List<FileHashInfo> relativizedAppPathFiles,
                            Set<Path> applicationRelativizedPathFolders,
                            List<FileHashInfo> cloudFiles,
                            Set<Path> cloudFolders) {
        this.applicationFiles = applicationAbsolutePathFiles;
        this.applicationRelativizedPathFiles = relativizedAppPathFiles;
        this.applicationFoldersRelativized = applicationRelativizedPathFolders;
        this.cloudFiles = cloudFiles;
        this.cloudFolders = cloudFolders;
    }

    /**
     * @return Set with {@link ua.wyverno.files.hashs.FileHashInfo} that are required delete from Cloud Storage
     */
    public Set<FileHashInfo> getDeletedFiles() {
        if (this.deletedFiles != null) return Collections.unmodifiableSet(this.deletedFiles);
        this.deletedFiles = this.getCloudFiles()
                .stream()
                .filter(file ->
                        this.getDeletedFolders()
                                .stream()
                                .noneMatch(deletedFolder -> file.getPathFile().startsWith(deletedFolder)))
                .filter(file -> !this.getApplicationRelativizedPathFiles().contains(file))
                .collect(Collectors.toSet());

        return Collections.unmodifiableSet(this.deletedFiles);
    }

    /**
     * @return Set with {@link ua.wyverno.files.hashs.FileHashInfo} that are required add files to Cloud Storage
     */
    public Set<FileHashInfo> getAddedOrModifyFiles() {
        if (this.addedOrModifyFiles != null) return Collections.unmodifiableSet(this.addedOrModifyFiles);

        this.addedOrModifyFiles = this.getApplicationRelativizedPathFiles()
                .stream()
                .filter(file -> !this.getCloudFiles().contains(file))
                .collect(Collectors.toSet());

        return Collections.unmodifiableSet(this.addedOrModifyFiles);
    }

    public Set<Path> getAddedFolders() {
        if (this.addedFolders != null) return Collections.unmodifiableSet(this.addedFolders);

        this.addedFolders = this.getApplicationFoldersRelativized()
                .stream()
                .filter(appFolder -> !this.getCloudFolders().contains(appFolder))
                .collect(Collectors.toSet());

        return Collections.unmodifiableSet(this.addedFolders);
    }

    /**
     * @return Set with {@link java.nio.file.Path} that are required delete folder from Cloud Storage
     */
    public Set<Path> getDeletedFolders() {
        if (this.deletedFolders != null) return Collections.unmodifiableSet(this.deletedFolders);

        Set<Path> checkedFolders = new HashSet<>();

        this.deletedFolders = new HashSet<>();
        this.getCloudFolders()
                .stream()
                .filter(cloudFolder -> this.deletedFolders.stream().noneMatch(cloudFolder::startsWith))
                .forEach(cloudFolder -> {
                    Path notCheckedRoot = cloudFolder;
                    while (notCheckedRoot.getParent() != null && !this.deletedFolders.contains(notCheckedRoot.getParent())
                            &&
                            !checkedFolders.contains(notCheckedRoot.getParent())) {
                        notCheckedRoot = notCheckedRoot.getParent();
                    }

                    final Path finalNotCheckedRoot = notCheckedRoot;
                    boolean notHasFileInFolder =  this.getApplicationRelativizedPathFiles()
                            .stream()
                            .noneMatch(file -> file.getPathFile().startsWith(finalNotCheckedRoot));

                    checkedFolders.add(finalNotCheckedRoot);
                    if (notHasFileInFolder) this.deletedFolders.add(finalNotCheckedRoot);
                });



        return Collections.unmodifiableSet(this.deletedFolders);
    }

    /**
     * @return Set with {@link java.nio.file.Path} - Application folders with Files
     */
    public Set<Path> getApplicationFoldersRelativized() {
//        if (this.applicationFoldersRelativized != null) return Collections.unmodifiableSet(applicationFoldersRelativized);
//
//        this.applicationFoldersRelativized = this.getApplicationRelativizedPathFiles()
//                .stream()
//                .map(file -> file.getPathFile().getParent())
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//        this.applicationFoldersRelativized.remove(Paths.get("/"));
        return Collections.unmodifiableSet(this.applicationFoldersRelativized);
    }

    /**
     * @return Set with {@link java.nio.file.Path} - Cloud Storage Folders with Files
     */
    public Set<Path> getCloudFolders() {
//        if (this.cloudFolders != null) return Collections.unmodifiableSet(this.cloudFolders);
//
//        this.cloudFolders = this.cloudFiles
//                .stream()
//                .map(file -> file.getPathFile().getParent())
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//        this.cloudFolders.remove(Paths.get("/"));
        return Collections.unmodifiableSet(this.cloudFolders);
    }

    /**
     * @return List with {@link ua.wyverno.files.hashs.FileHashInfo} - Application Files
     */
    public List<FileHashInfo> getApplicationRelativizedPathFiles() {
        return Collections.unmodifiableList(applicationRelativizedPathFiles);
    }
    public List<FileHashInfo> getApplicationFiles() {
        return Collections.unmodifiableList(this.applicationFiles);
    }

    /**
     * @return List with {@link ua.wyverno.files.hashs.FileHashInfo} - Cloud Storage Files
     */
    public List<FileHashInfo> getCloudFiles() {
        return Collections.unmodifiableList(this.cloudFiles);
    }

    public synchronized void synchronizedWithCloudStorage(DropBoxAPI dropBoxAPI, Path root) throws DbxException, IOException {
        Set<FileHashInfo> deletedFiles = this.getDeletedFiles();
        Set<FileHashInfo> addedFiles = this.getAddedOrModifyFiles();
        Set<Path> addedFolders = this.getAddedFolders();
        Set<Path> deletedFolders = this.getDeletedFolders();
        Set<Path> cloudFolders = this.getCloudFolders();
        Set<Path> appFolders = this.getApplicationFoldersRelativized();

        for (Path appFolder : appFolders) {
            logger.info("App.Folder: {}", appFolder);
        }
        for (Path cloudFolder : cloudFolders) {
            logger.info("Cloud Folder: {}", cloudFolder);
        }

        for (Path deletedFolder : deletedFolders) {
            logger.info("Deleted Folder: {}",deletedFolder);
        }
        if (!deletedFolders.isEmpty()) {
            dropBoxAPI.deleteFiles(deletedFolders
                    .stream()
                    .map(Path::toString)
                    .toList());
        } else {
            logger.info("Not need delete folders in cloud storage!");
        }

        for (FileHashInfo file : deletedFiles) {
            logger.info("\nDeleted files: {}\nHash: {}", file.getPathFile(), file.getHash());
        }
        if (!deletedFiles.isEmpty()) {
            dropBoxAPI.deleteFiles(deletedFiles
                    .stream()
                    .map(file -> file.getPathFile().toString())
                    .toList());
        } else {
            logger.info("Not need delete file in cloud storage!");
        }

        for (Path addedFolder : addedFolders) {
            logger.info("Added folder: {}", addedFolder);
        }
        if (!addedFolders.isEmpty()) {
            dropBoxAPI.createFolders(addedFolders
                    .stream()
                    .map(Path::toString)
                    .toList());
        } else {
            logger.info("Not need add folders in cloud storage!");
        }

        for (FileHashInfo file : addedFiles) {
            logger.info("\nAdded or Modify: {}\nHash: {}", file.getPathFile(), file.getHash());
        }
        if (!addedFiles.isEmpty()) {
            dropBoxAPI.uploadFiles(addedFiles
                    .stream()
                    .map(file -> {
                        Path cloudFile = file.getPathFile();
                        Path originalPath = root.resolve(Paths.get("/").relativize(cloudFile));
                        return new CloudLocalFile(originalPath, cloudFile);
                    }).toList());
        } else {
            logger.info("Not need add files in cloud storage!");
        }


        logger.info("Synchronized with Cloud Storage is complete!");
    }
}
