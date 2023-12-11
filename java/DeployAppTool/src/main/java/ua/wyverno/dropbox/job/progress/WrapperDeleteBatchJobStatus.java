package ua.wyverno.dropbox.job.progress;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.DeleteBatchJobStatus;

public class WrapperDeleteBatchJobStatus extends JobStatus {

    private DeleteBatchJobStatus deleteBatchJobStatus;

    public WrapperDeleteBatchJobStatus(DbxUserFilesRequests filesRequests, String asyncJobID) throws DbxException {
        super(filesRequests, asyncJobID);
    }

    @Override
    public void createWrapper() throws DbxException {
        this.deleteBatchJobStatus = filesRequests.deleteBatchCheck(asyncJobID);
    }

    @Override
    public TagProgress getTagProgress() {
        switch (this.deleteBatchJobStatus.tag()) {
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
        return this.deleteBatchJobStatus.isInProgress();
    }

    @Override
    public boolean isComplete() {
        return this.deleteBatchJobStatus.isComplete();
    }

    @Override
    public boolean isFailed() {
        return this.deleteBatchJobStatus.isFailed();
    }

    @Override
    public boolean isOther() {
        return this.deleteBatchJobStatus.isOther();
    }

    public DeleteBatchJobStatus getOriginal() {
        return this.deleteBatchJobStatus;
    }
}
