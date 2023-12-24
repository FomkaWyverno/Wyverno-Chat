package ua.wyverno.dropbox.files.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class UploadFile {

    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);

    private Queue<ChunkFile> chunksFile;
    private int sizeFile;
    public UploadFile(Path pathFile, int chunkSize) throws IOException {
        try (FileInputStream fileIStream = new FileInputStream(pathFile.toFile())) {
            LinkedList<ChunkFile> linkedList = new LinkedList<>();
            int offset = 0;
            int readBytes = 0;
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

    public int size() {
        return this.sizeFile;
    }
}
