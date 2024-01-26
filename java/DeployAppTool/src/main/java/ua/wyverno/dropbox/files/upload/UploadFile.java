package ua.wyverno.dropbox.files.upload;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.files.CloudLocalFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * {@link ua.wyverno.dropbox.files.upload.UploadFile} class - its purpose is to upload file to DropBox
 * <p style="color: #ff4fbe; font-weight: bold;">Fields:</p>
 * <p><span style="color:#d6676f;"> <span style="color:#b9a670">Queue&lt;ChunkFile&gt;</span> chunksFile</span> - it a queue necessary for storing file chunks and will be used for uploading it the future</p>
 * <p><span style="color:#d6676f;"> <span style="color:#b9a670">String</span> sessionID</span> - DropBox uploading session id</p>
 * <p><span style="color:#d6676f;"> <span style="color:#b9a670">DbxUserFilesRequests</span> files</span> - Object for managing requests for file management in DropBox</p>
 * <p><span style="color:#d6676f;"> <span style="color:#b9a670">String</span> cloudFile</span> - Path to Cloud File in DropBox</p>
 * <p><span style="color:#d6676f;"> <span style="color:#b9a670">int</span> sizeFile</span> - Size File</p>
 */
public class UploadFile {

    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);

    private final Queue<ChunkFile> chunksFile;
    private String sessionID;
    private final DbxUserFilesRequests files;
    private final String cloudFile;

    private int sizeFile;

    /**
     *
     * @param uploadFile object with local file and cloud file path in DropboxAPI
     * @param files Object for managing requests for file management in DropBox
     * @param chunkSize max size for chunk file
     * @throws IOException problem in reading local file
     */
    public UploadFile(CloudLocalFile uploadFile, DbxUserFilesRequests files, int chunkSize) throws IOException {
        this.files = files;
        this.cloudFile = uploadFile.cloudFile().toString().replace("\\","/");;
        try (FileInputStream fileIStream = new FileInputStream(uploadFile.localFile().toFile())) {
            LinkedList<ChunkFile> linkedList = new LinkedList<>();
            int offset = 0;
            int readBytes;
            byte[] buffer = new byte[chunkSize];

            byte[] latestBuffer = new byte[0];
            while ((readBytes = fileIStream.read(buffer)) != -1) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                ChunkFile chunkFile = new ChunkFile(offset, readBytes, byteArrayInputStream);
                offset = offset + readBytes;
                linkedList.offer(chunkFile);


                this.sizeFile += readBytes;
                if (readBytes < chunkSize) {
                    latestBuffer = Arrays.copyOf(buffer, readBytes);
                }
                buffer = new byte[chunkSize];
            }

            if (latestBuffer.length > 0) {
                ChunkFile chunk = linkedList.removeLast();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(latestBuffer);
                ChunkFile newChunk = new ChunkFile(chunk.getOffset(), latestBuffer.length, byteArrayInputStream);
                linkedList.offer(newChunk);
            }

            this.chunksFile = linkedList;
        }
    }

    public Queue<ChunkFile> getChunksFile() {
        return chunksFile;
    }

    public synchronized void upload(String sessionID) throws IOException, DbxException {
        this.sessionID = sessionID;
        this.uploadSessionAppend();
        this.finishSessionUpload();
    }

    /**
     * Called to endpoint DropBox API /upload_session/append_v2
     * And uploading file to Dropbox API by chunk file
     * @throws DbxException creating when uploading file to Dropbox or creating UploadSessionAppendV2Uploader object
     * UploadSessionAppendV2Uploader uploaderAppend = files.uploadSessionAppendV2(cursor);
     * @throws IOException creating when uploading file
     */
    private void uploadSessionAppend() throws DbxException, IOException {
        while (this.chunksFile.size() > 1) {
            logger.trace("Call to DropBox /upload_session/append_v2");
            ChunkFile chunkFile = Objects.requireNonNull(this.chunksFile.poll());
            UploadSessionCursor cursor = new UploadSessionCursor(this.sessionID, chunkFile.getOffset());

            logger.trace("Append-Cursor: {}", cursor.toStringMultiline());

            UploadSessionAppendV2Uploader uploaderAppend = files.uploadSessionAppendV2(cursor);
            uploaderAppend.uploadAndFinish(Objects.requireNonNull(chunkFile).getInputStream());
            uploaderAppend.close();
            logger.trace("Append success - chunks left - {}", this.chunksFile.size());
        }
    }

    /**
     * Finish uploading file and close session for upload
     * @throws DbxException can throw when creating UploadSessionFinishUploader
     * @throws IOException generate when have problem with upload file
     */
    private void finishSessionUpload() throws DbxException, IOException {
        ChunkFile lastChunk = Objects.requireNonNull(this.chunksFile.poll());
        UploadSessionCursor cursor = new UploadSessionCursor(this.sessionID, lastChunk.getOffset());
        CommitInfo commitInfo = new CommitInfo(this.cloudFile);
        logger.trace("Call to DropBox /upload_session/finish");

        logger.trace("Finish-Cursor: {}",cursor.toStringMultiline());
        logger.trace("Finish-Commit: {}",commitInfo.toStringMultiline());
        UploadSessionFinishUploader finishUploader = this.files.uploadSessionFinish(cursor, commitInfo);
        finishUploader.uploadAndFinish(lastChunk.getInputStream());

        finishUploader.close();
    }

    public int size() {
        return this.sizeFile;
    }
}
