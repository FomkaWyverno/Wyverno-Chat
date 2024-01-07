package ua.wyverno.dropbox;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.create.folder.result.WrapperCreateFolderResult;
import ua.wyverno.dropbox.files.CloudLocalFile;
import ua.wyverno.dropbox.files.upload.ChunkFile;
import ua.wyverno.dropbox.files.upload.UploadFile;
import ua.wyverno.dropbox.job.progress.JobStatus;
import ua.wyverno.dropbox.job.progress.WrapperDeleteBatchJobStatus;
import ua.wyverno.dropbox.metadata.FolderMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DropBoxAPI {

    private static final Logger logger = LoggerFactory.getLogger(DropBoxAPI.class);
    private static final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/client").build();

    private DbxClientV2 dbxClientV2;

    public DropBoxAPI(String accessToken) {
        this.dbxClientV2 = new DbxClientV2(config, accessToken);
    }

    public boolean isValidAccessToken() throws DbxException {
        try {
            this.dbxClientV2.check().user().getResult();
            return true;
        } catch (InvalidAccessTokenException | BadRequestException e) {
            return false;
        }
    }

    public void deleteFile(String folderPath) throws DbxException {
        if (!folderPath.isBlank()) {
            this.deletePaths(Collections.singletonList(new DeleteArg(folderPath.toLowerCase())));
        } else {
            DbxUserFilesRequests files = this.dbxClientV2.files();

            List<DeleteArg> pathList = new ArrayList<>();
            ListFolderResult listFolder = files.listFolder(folderPath);
            while (true) {
                for (Metadata metadata : listFolder.getEntries()) {
                    pathList.add(new DeleteArg(metadata.getPathLower()));
                }
                if (!listFolder.getHasMore()) {
                    break;
                }
                listFolder = this.dbxClientV2.files().listFolderContinue(listFolder.getCursor());
            }
            this.deletePaths(pathList);
        }
    }

    public void deleteFiles(List<String> foldersPath) throws DbxException {
        this.deletePaths(foldersPath
                .stream()
                .map(DeleteArg::new)
                .toList());
    }

    private void deletePaths(List<DeleteArg> paths) throws DbxException {
        DbxUserFilesRequests files = this.dbxClientV2.files();

        paths.forEach(e -> logger.debug("Try delete from DropBox: {}", e.getPath()));
        String asyncJobId = files.deleteBatch(paths).getAsyncJobIdValue();

        WrapperDeleteBatchJobStatus jobStatus = new WrapperDeleteBatchJobStatus(files, asyncJobId);

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

    public void createFolder(String folderPath) throws DbxException {
        DbxUserFilesRequests files = this.dbxClientV2.files();

        CreateFolderResult createFolderResult = files.createFolderV2(folderPath);

        logger.debug("Create folder: {}", createFolderResult.getMetadata().getPathDisplay());
    }

    public void createFolders(List<String> foldersUpload) throws DbxException, JsonProcessingException {
        DbxUserFilesRequests filesRequest = this.dbxClientV2.files();

        CreateFolderBatchLaunch apiCreateFolders = filesRequest.createFolderBatch(foldersUpload);


        logger.info("Created files");

        ObjectMapper mapper = new ObjectMapper();

        for (CreateFolderBatchResultEntry entry : apiCreateFolders.getCompleteValue().getEntries()) {
            WrapperCreateFolderResult result = mapper.readValue(entry.toStringMultiline(), WrapperCreateFolderResult.class);

            logger.info("Create folder: {}", result.getMetadata().getPathDisplay());
        }

    }

    public void uploadFile(CloudLocalFile fileUpload) throws DbxException, IOException {
        DbxUserFilesRequests files = this.dbxClientV2.files();
        File localFile = fileUpload.getLocalFile().toFile();
        long sizeFile = localFile.length();

        if (sizeFile >= 1024*1024) {
            logger.debug("Calls to uploadFile()\nFile = {}\nFile size = {}MB", fileUpload.getLocalFile(), String.format("%.2f", (double) sizeFile/1024/1024));
        } else {
            logger.debug("Calls to uploadFile()\nFile = {}\nFile size = {}KB", fileUpload.getLocalFile(), String.format("%.2f",(double) sizeFile/1024));
        }

        int chunkSize = 150*1024*1024; // 150 MB

        if (sizeFile <= chunkSize) {
            logger.trace("Call to DropBox /upload endpoint");
            UploadUploader uploader = files.upload(fileUpload.getCloudFile().toString().replace("\\","/"));
            uploader.uploadAndFinish(Files.newInputStream(localFile.toPath()));
        } else {
            logger.trace("Call to DropBox /upload_session/start");

            UploadSessionStartUploader uploader = files.uploadSessionStart();

            UploadFile uploadFile = new UploadFile(fileUpload, files, chunkSize);
            Queue<ChunkFile> chunks = uploadFile.getChunksFile();
            ChunkFile chunk = Objects.requireNonNull(chunks.poll());

            UploadSessionStartResult uploadSessionStartResult = uploader
                    .uploadAndFinish(Objects.requireNonNull(chunk.getInputStream()));

            uploader.close();

            uploadFile.upload(uploadSessionStartResult.getSessionId());
        }
    }

    public void uploadFiles(List<CloudLocalFile> filesUpload) throws DbxException, IOException {
        if (filesUpload.size() == 1) this.uploadFile(filesUpload.get(0));
        DbxUserFilesRequests files = this.dbxClientV2.files();

        logger.trace("Call to DropBox /upload_session/start_batch");
        UploadSessionStartBatchResult uploaderStart = files.uploadSessionStartBatch(filesUpload.size());
        int chunkSize = 150 * 1024 * 1024;

        List<UploadSessionFinishArg> finishSessionArgs = new ArrayList<>();

        for (int i = 0; i < filesUpload.size(); i++) {
            CloudLocalFile fileUpload = filesUpload.get(i);
            String sessionId = uploaderStart.getSessionIds().get(i);

            File localFile = fileUpload.getLocalFile().toFile();
            File cloudFile = fileUpload.getCloudFile().toFile();

            long sizeFile = localFile.length();

            if (sizeFile >= 1024*1024) {
                logger.debug("Calls to uploadFiles()\nFile = {}\nFile size = {}MB", localFile, String.format("%.2f", (double) sizeFile/1024/1024));
            } else {
                logger.debug("Calls to uploadFiles()\nFile = {}\nFile size = {}KB", localFile, String.format("%.2f",(double) sizeFile/1024));
            }

            UploadFile uploadFile = new UploadFile(fileUpload, files, chunkSize);
            uploadFile.upload(sessionId);

            CommitInfo commitInfo = new CommitInfo(cloudFile.toString().replace("\\","/"));
            UploadSessionCursor cursorFile = new UploadSessionCursor(sessionId,uploadFile.size());


            UploadSessionFinishArg sessionFinishArg = new UploadSessionFinishArg(cursorFile, commitInfo);
            finishSessionArgs.add(sessionFinishArg);
        }
        logger.trace("Call to DropBox - /upload_session/finish_batch");
        List<UploadSessionFinishBatchResultEntry> listEntries = files.uploadSessionFinishBatchV2(finishSessionArgs).getEntries();

        for (UploadSessionFinishBatchResultEntry entry : listEntries) {
            logger.trace("Result Upload files Batch Entry: {}", entry.toStringMultiline());
        }
    }

    public List<FolderMetadata> getListWithAllFolders(String path) throws DbxException, JsonProcessingException {
        DbxUserFilesRequests files = this.dbxClientV2.files();

        List<FolderMetadata> resultFoldersList = new ArrayList<>();

        logger.debug("Collect path to folders in path \"{}\". Call to DropBox API Endpoint /list_folder",path);
        ListFolderResult result = files.listFolder(path);

        ObjectMapper mapper = new ObjectMapper();
        while (true) {
            for (Metadata metadata : result.getEntries()) {

                JsonNode metadataNode = mapper.readTree(metadata.toStringMultiline());

                if (metadataNode.path(".tag").asText().equals("folder")) {
                    logger.trace("API Endpoint /list_folder(\"{}\") result entry path: {}", path, metadata.getPathDisplay());

                    FolderMetadata folderMetadata = mapper.treeToValue(metadataNode, FolderMetadata.class);
                    resultFoldersList.add(folderMetadata);
                    resultFoldersList.addAll(this.getListWithAllFolders(folderMetadata.getPathLower()));
                }

            }

            if (!result.getHasMore()) break;
            result = files.listFolderContinue(result.getCursor());
        }

        return resultFoldersList;
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
