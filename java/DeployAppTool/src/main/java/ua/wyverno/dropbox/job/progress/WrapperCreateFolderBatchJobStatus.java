package ua.wyverno.dropbox.job.progress;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.CreateFolderBatchJobStatus;
import com.dropbox.core.v2.files.DbxUserFilesRequests;

public class WrapperCreateFolderBatchJobStatus extends JobStatus {

    private CreateFolderBatchJobStatus createFolderBatchJobStatus;

    public WrapperCreateFolderBatchJobStatus(DbxUserFilesRequests filesRequests, String asyncJobID) throws DbxException {
        super(filesRequests, asyncJobID);
    }

    @Override
    public void createWrapper() throws DbxException {
        this.createFolderBatchJobStatus = filesRequests.createFolderBatchCheck(asyncJobID);
    }

    @Override
    public TagProgress getTagProgress() {
        switch (this.createFolderBatchJobStatus.tag()) {
            case IN_PROGRESS -> {
                return TagProgress.IN_PROGRESS;
            }
            case COMPLETE -> {
                return TagProgress.COMPLETE;
            }
            case FAILED -> {
                return TagProgress.FAILED;
            }
            case OTHER -> {
                return TagProgress.OTHER;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public boolean isInProgress() {
        return this.createFolderBatchJobStatus.isInProgress();
    }

    @Override
    public boolean isComplete() {
        return this.createFolderBatchJobStatus.isComplete();
    }

    @Override
    public boolean isFailed() {
        return this.createFolderBatchJobStatus.isFailed();
    }

    @Override
    public boolean isOther() {
        return this.createFolderBatchJobStatus.isOther();
    }

    public CreateFolderBatchJobStatus getOriginal() {
        return this.createFolderBatchJobStatus;
    }
}
