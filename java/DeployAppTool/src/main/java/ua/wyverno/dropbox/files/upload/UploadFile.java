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

public class UploadFile {

    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);

    private Queue<ChunkFile> chunksFile;
    private String sessionID;
    private final DbxUserFilesRequests files;
    private final String cloudFile;

    private int sizeFile;
    public UploadFile(CloudLocalFile uploadFile, DbxUserFilesRequests files, int chunkSize) throws IOException {
        this.files = files;
        this.cloudFile = uploadFile.getCloudFile().toString().replace("\\","/");;
        try (FileInputStream fileIStream = new FileInputStream(uploadFile.getLocalFile().toFile())) {
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
