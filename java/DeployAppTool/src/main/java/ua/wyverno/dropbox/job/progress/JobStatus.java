package ua.wyverno.dropbox.job.progress;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DbxUserFilesRequests;

/**
 * Wrapper for JobStatus to generalize and make synchronization easier
 */
public abstract class JobStatus {

    protected final DbxUserFilesRequests filesRequests;
    protected final String asyncJobID;
    public JobStatus(DbxUserFilesRequests filesRequests, String asyncJobID) throws DbxException {
        this.filesRequests = filesRequests;
        this.asyncJobID = asyncJobID;
        this.createWrapper();
    }

    public abstract void createWrapper() throws DbxException;
    public abstract TagProgress getTagProgress();
    public abstract boolean isInProgress();
    public abstract boolean isComplete();
    public abstract boolean isFailed();
    public abstract boolean isOther();
}
