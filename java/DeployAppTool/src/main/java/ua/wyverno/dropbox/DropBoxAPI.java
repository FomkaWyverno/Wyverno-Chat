package ua.wyverno.dropbox;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.job.progress.JobStatus;
import ua.wyverno.dropbox.job.progress.WrapperCreateFolderBatchJobStatus;
import ua.wyverno.dropbox.job.progress.WrapperDeleteBatchJobStatus;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public void deleteAllFromFolder(String folderPath) throws DbxException {
        DbxUserFilesRequests files = this.dbxClientV2.files();
        ListFolderResult listFolder = files.listFolder(folderPath);

        List<DeleteArg> pathList = new ArrayList<>();
        while (true) {
            for (Metadata metadata : listFolder.getEntries()) {
                pathList.add(new DeleteArg(metadata.getPathLower()));
            }
            if (!listFolder.getHasMore()) {
                break;
            }
            listFolder = this.dbxClientV2.files().listFolderContinue(listFolder.getCursor());
        }

        logger.info("Try Delete from DropBox!");
        pathList.forEach(e -> logger.info(e.getPath()));

        String asyncJobId = files.deleteBatch(pathList).getAsyncJobIdValue();

        WrapperDeleteBatchJobStatus jobStatus = new WrapperDeleteBatchJobStatus(files, asyncJobId);

        this.waitUntilJobComplete(jobStatus, "/delete_batch");

        if (jobStatus.isComplete()) {
            logger.info("Complete delete from ./{}", folderPath);

            DeleteBatchResult batchResult = jobStatus.getOriginal().getCompleteValue();

            List<DeleteBatchResultEntry> entries = batchResult.getEntries();

            for (DeleteBatchResultEntry entry : entries) {
                logger.info("Deleted: {}",entry.getSuccessValue().getMetadata().getPathDisplay());
            }
        } else if (jobStatus.isFailed()) {
            logger.warn("Not complete delete folder from ./{}", folderPath);
        } else if (jobStatus.isOther()) {
            logger.warn("JobStatus is Other");
        } else {
            logger.warn("JobStatus unknown");
        }
    }

    public void uploadFiles(String cloudFolderPath, List<String> filesUpload, List<String> foldersUpload) throws DbxException {
        DbxUserFilesRequests filesRequest = this.dbxClientV2.files();
        filesUpload = filesUpload.stream()
                                 .map(pathFile -> cloudFolderPath+pathFile)
                                 .collect(Collectors.toList());

        CreateFolderBatchLaunch apiCreateFolders = filesRequest.createFolderBatch(foldersUpload);

        WrapperCreateFolderBatchJobStatus jobStatus =
                new WrapperCreateFolderBatchJobStatus(filesRequest, apiCreateFolders.getAsyncJobIdValue());

        this.waitUntilJobComplete(jobStatus, "/create_folder_batch/check");

        if (jobStatus.isComplete()) {
            logger.info("Created files");
        }
    }

    private void waitUntilJobComplete(JobStatus jobStatus, String descriptionJob) throws DbxException {
        while (jobStatus.isInProgress()) {
            try {
                logger.info("Waiting for {} to complete. Status: {}",descriptionJob, jobStatus.getTagProgress());
                TimeUnit.SECONDS.sleep(1);
                jobStatus.createWrapper();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
