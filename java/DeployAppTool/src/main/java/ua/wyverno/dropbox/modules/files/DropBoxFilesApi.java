package ua.wyverno.dropbox.modules.files;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.create.folder.result.WrapperCreateFolderResult;
import ua.wyverno.dropbox.exceptions.UnknownMetadataTypeException;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.dropbox.files.upload.ChunkFile;
import ua.wyverno.dropbox.files.upload.UploadFile;
import ua.wyverno.dropbox.job.progress.JobStatus;
import ua.wyverno.dropbox.job.progress.WrapperCreateFolderBatchJobStatus;
import ua.wyverno.dropbox.job.progress.WrapperDeleteBatchJobStatus;
import ua.wyverno.dropbox.metadata.FileMetadata;
import ua.wyverno.dropbox.metadata.FolderMetadata;
import ua.wyverno.dropbox.metadata.MetadataContainer;
import ua.wyverno.dropbox.modules.IFilesAPI;
import ua.wyverno.files.hashs.CloudFileMetadataNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DropBoxFilesApi implements IFilesAPI {

    private static final Logger logger = LoggerFactory.getLogger(DropBoxFilesApi.class);

    private final DbxUserFilesRequests dbxFiles;

    public DropBoxFilesApi(DbxUserFilesRequests dbxFiles) {
        this.dbxFiles = dbxFiles;
    }


    @Override
    public void deleteFile(String folderPath) throws DbxException {
        if (!folderPath.isBlank()) {
            this.deletePaths(Collections.singletonList(new DeleteArg(folderPath.toLowerCase())));
        } else {
            List<DeleteArg> pathList = new ArrayList<>();
            ListFolderResult listFolder = this.dbxFiles.listFolder(folderPath);
            while (true) {
                for (Metadata metadata : listFolder.getEntries()) {
                    pathList.add(new DeleteArg(metadata.getPathLower()));
                }
                if (!listFolder.getHasMore()) {
                    break;
                }
                listFolder = this.dbxFiles.listFolderContinue(listFolder.getCursor());
            }
            this.deletePaths(pathList);
        }
    }

    @Override
    public void deleteFiles(List<String> foldersPath) throws DbxException {
        this.deletePaths(foldersPath
                .stream()
                .map(folderPath -> new DeleteArg(folderPath.replace("\\","/")))
                .toList());
    }

    private void deletePaths(List<DeleteArg> paths) throws DbxException {
        paths.forEach(e -> logger.debug("Try delete from DropBox: {}", e.getPath()));
        String asyncJobId = this.dbxFiles.deleteBatch(paths).getAsyncJobIdValue();

        WrapperDeleteBatchJobStatus jobStatus = new WrapperDeleteBatchJobStatus(this.dbxFiles, asyncJobId);

        this.waitUntilJobComplete(jobStatus, "/delete_batch");

        if (jobStatus.isComplete()) {
            logger.info("Complete delete from ./{}", paths);

            DeleteBatchResult batchResult = jobStatus.getOriginal().getCompleteValue();

            List<DeleteBatchResultEntry> entries = batchResult.getEntries();

            for (DeleteBatchResultEntry entry : entries) {
                logger.info("Complete delete: {}", entry.getSuccessValue().getMetadata().getPathDisplay());
            }
        } else if (jobStatus.isFailed()) {
            logger.warn("Not complete delete ./{}", paths);
        } else if (jobStatus.isOther()) {
            logger.warn("JobStatus is Other");
        } else {
            logger.warn("JobStatus unknown");
        }
    }

    @Override
    public void createFolder(String folderPath) throws DbxException {
        CreateFolderResult createFolderResult = this.dbxFiles.createFolderV2(folderPath);
        logger.debug("Create folder: {}", createFolderResult.getMetadata().getPathDisplay());
    }

    @Override
    public void createFolders(List<String> foldersUpload) throws DbxException {
        foldersUpload = foldersUpload
                .stream()
                .map(folderPath -> folderPath.replace("\\", "/"))
                .toList();

        foldersUpload.forEach(folder -> logger.trace("Try create folder: {}", folder));
        CreateFolderBatchLaunch apiCreateFolders = this.dbxFiles.createFolderBatch(foldersUpload);


        ObjectMapper mapper = new ObjectMapper();

        CreateFolderBatchResult result;
        if (apiCreateFolders.isAsyncJobId()) {
            logger.debug("Creating folder is async!");
            String asyncID = apiCreateFolders.getAsyncJobIdValue();
            WrapperCreateFolderBatchJobStatus createFolderJobProgress = new WrapperCreateFolderBatchJobStatus(this.dbxFiles, asyncID);
            waitUntilJobComplete(createFolderJobProgress, "/create_folder_batch/check");

            if (createFolderJobProgress.isComplete()) {
                result = createFolderJobProgress.getOriginal().getCompleteValue();
            } else {
                throw new DbxException("Not complete async method!\n{}", createFolderJobProgress.getOriginal().toStringMultiline());
            }
        } else {
            result = apiCreateFolders.getCompleteValue();
        }

        logger.info("Created folders!");
        try {
            for (CreateFolderBatchResultEntry entry : result.getEntries()) {
                WrapperCreateFolderResult resultEntry = mapper.readValue(entry.toStringMultiline(), WrapperCreateFolderResult.class);

                logger.info("Create folder: {}", resultEntry.getMetadata().getPathDisplay());
            }
        } catch (JsonProcessingException e) {
            logger.error("When create resultEntry throws exception",e);
        }
    }

    @Override
    public void uploadFile(CloudLocalFile fileUpload) throws DbxException, IOException {
        File localFile = fileUpload.localFile().toFile();
        long sizeFile = localFile.length();

        if (sizeFile >= 1024 * 1024) {
            logger.debug("Calls to uploadFile()\nFile = {}\nFile size = {}MB", fileUpload.localFile(), String.format("%.2f", (double) sizeFile / 1024 / 1024));
        } else {
            logger.debug("Calls to uploadFile()\nFile = {}\nFile size = {}KB", fileUpload.localFile(), String.format("%.2f", (double) sizeFile / 1024));
        }

        int chunkSize = 150 * 1024 * 1024; // 150 MB

        if (sizeFile <= chunkSize) {
            logger.trace("Call to DropBox /upload endpoint");
            UploadUploader uploader = this.dbxFiles.upload(fileUpload.cloudFile().toString().replace("\\", "/"));
            uploader.uploadAndFinish(Files.newInputStream(localFile.toPath()));
        } else {
            logger.trace("Call to DropBox /upload_session/start");

            UploadSessionStartUploader uploader = this.dbxFiles.uploadSessionStart();

            UploadFile uploadFile = new UploadFile(fileUpload, this.dbxFiles, chunkSize);
            Queue<ChunkFile> chunks = uploadFile.getChunksFile();
            ChunkFile chunk = Objects.requireNonNull(chunks.poll());

            UploadSessionStartResult uploadSessionStartResult = uploader
                    .uploadAndFinish(Objects.requireNonNull(chunk.getInputStream()));

            uploader.close();

            uploadFile.upload(uploadSessionStartResult.getSessionId());
        }
    }

    @Override
    public void uploadFiles(List<CloudLocalFile> filesUpload) throws DbxException, IOException {
        if (filesUpload.size() == 1) this.uploadFile(filesUpload.get(0));

        logger.trace("Call to DropBox /upload_session/start_batch");
        UploadSessionStartBatchResult uploaderStart = this.dbxFiles.uploadSessionStartBatch(filesUpload.size());
        int chunkSize = 150 * 1024 * 1024;

        List<UploadSessionFinishArg> finishSessionArgs = new ArrayList<>();

        for (int i = 0; i < filesUpload.size(); i++) {
            CloudLocalFile fileUpload = filesUpload.get(i);
            String sessionId = uploaderStart.getSessionIds().get(i);

            File localFile = fileUpload.localFile().toFile();
            File cloudFile = fileUpload.cloudFile().toFile();

            long sizeFile = localFile.length();

            if (sizeFile >= 1024 * 1024) {
                logger.debug("Calls to uploadFiles()\nFile = {}\nFile size = {}MB", localFile, String.format("%.2f", (double) sizeFile / 1024 / 1024));
            } else {
                logger.debug("Calls to uploadFiles()\nFile = {}\nFile size = {}KB", localFile, String.format("%.2f", (double) sizeFile / 1024));
            }

            UploadFile uploadFile = new UploadFile(fileUpload, this.dbxFiles, chunkSize);
            uploadFile.upload(sessionId);

            CommitInfo commitInfo = new CommitInfo(cloudFile.toString().replace("\\", "/"));
            UploadSessionCursor cursorFile = new UploadSessionCursor(sessionId, uploadFile.size());


            UploadSessionFinishArg sessionFinishArg = new UploadSessionFinishArg(cursorFile, commitInfo);
            finishSessionArgs.add(sessionFinishArg);
        }
        logger.trace("Call to DropBox - /upload_session/finish_batch");
        List<UploadSessionFinishBatchResultEntry> listEntries = this.dbxFiles.uploadSessionFinishBatchV2(finishSessionArgs).getEntries();

        for (UploadSessionFinishBatchResultEntry entry : listEntries) {
            logger.trace("Result Upload files Batch Entry: {}", entry.toStringMultiline());
        }
    }

    @Override
    public MetadataContainer getListFolderAsMetadataContainer(String path) throws DbxException {
        MetadataContainer container = new MetadataContainer();

        logger.debug("Call to DropBox API Endpoint /list_folder path: {}", path);
        ListFolderResult result = this.dbxFiles.listFolder(path);
        ObjectMapper mapper = new ObjectMapper();
        try {
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    String metadataJsonString = metadata.toStringMultiline();
                    logger.trace("Entry Json-metadata: {}", metadataJsonString);
                    JsonNode metadataNode = mapper.readTree(metadataJsonString);
                    String tag = metadataNode.path(".tag").asText();
                    if (tag.equals("file")) {
                        FileMetadata fileMetadata = mapper.treeToValue(metadataNode, FileMetadata.class);
                        container.addFileMetadata(fileMetadata);
                    } else if (tag.equals("folder")) {
                        FolderMetadata folderMetadata = mapper.treeToValue(metadataNode, FolderMetadata.class);
                        container.addFolderMetadata(folderMetadata);
                    } else {
                        throw new UnknownMetadataTypeException("Unknown Metadata type! Json response:\n" + metadataJsonString);
                    }
                }
                if (!result.getHasMore()) break;
                logger.trace("Content in folder \"{}\" has more. Call to DropBox API Endpoint /list_folder/continue", path);
                result = this.dbxFiles.listFolderContinue(result.getCursor());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return container;
    }

    @Override
    public MetadataContainer collectAllContentFromPathAsMetadataContainer(String path) throws DbxException {
        boolean isRootPath = path.equals("");

        logger.info("Start collect all content as MetadataContainer from {} path in DropBox", isRootPath ? "root" : path);

        MetadataContainer container = this.getListFolderAsMetadataContainer(path);

        MetadataContainer resultContainer = new MetadataContainer();
        resultContainer.addMetadataContainer(container);
        for (FolderMetadata folderMetadata : container.getFolderMetadataList()) {
            MetadataContainer folderContainer = this.collectAllContentFromPathAsMetadataContainer(folderMetadata.getPathLower());
            resultContainer.addMetadataContainer(folderContainer);
        }

        logger.info("End collect all content from {} path in DropBox",isRootPath ? "root" : path);
        return resultContainer;
    }

    @Override
    public CloudFileMetadataNode collectRootContentAsCloudFileHashNode() throws DbxException {

        logger.info("Start collect all content as CloudFileMetadataNode from root path in DropBox");
        CloudFileMetadataNode rootCloudFile = new CloudFileMetadataNode(".",false);
        this.recursiveCollectAllContentAsCloudFileNodeHash(rootCloudFile,"");
        logger.info("End collect all content as CloudFileMetadataNode from root path in DropBox");
        return rootCloudFile;
    }

    private void recursiveCollectAllContentAsCloudFileNodeHash(CloudFileMetadataNode parent, String path) throws DbxException {
        MetadataContainer metadataContainer = this.getListFolderAsMetadataContainer(path);

        List<FileMetadata> fileMetadataList = metadataContainer.getFileMetadataList();
        List<FolderMetadata> folderMetadataList = metadataContainer.getFolderMetadataList();

        fileMetadataList.forEach(fileMetadata -> {
            CloudFileMetadataNode cloudFile = new CloudFileMetadataNode(parent, fileMetadata.getName(), fileMetadata.getContentHash(), true);
            parent.addChild(cloudFile);
        });
        for (FolderMetadata folderMetadata : folderMetadataList) {
            CloudFileMetadataNode cloudFolder = new CloudFileMetadataNode(parent, folderMetadata.getName(), false);
            parent.addChild(cloudFolder);
            this.recursiveCollectAllContentAsCloudFileNodeHash(cloudFolder, folderMetadata.getPathLower());
        }
    }

    private void waitUntilJobComplete(JobStatus jobStatus, String descriptionJob) throws DbxException {
        while (jobStatus.isInProgress()) {
            try {
                logger.info("Waiting for {} to complete. Status: {}", descriptionJob, jobStatus.getTagProgress());
                TimeUnit.SECONDS.sleep(1);
                jobStatus.createWrapper();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
