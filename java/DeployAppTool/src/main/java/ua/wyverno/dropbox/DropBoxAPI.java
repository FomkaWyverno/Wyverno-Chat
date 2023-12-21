package ua.wyverno.dropbox;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.create.folder.result.WrapperCreateFolderResult;
import ua.wyverno.dropbox.job.progress.JobStatus;
import ua.wyverno.dropbox.job.progress.WrapperDeleteBatchJobStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
