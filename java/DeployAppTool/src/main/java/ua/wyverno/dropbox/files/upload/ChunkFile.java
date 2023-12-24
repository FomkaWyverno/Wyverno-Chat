package ua.wyverno.dropbox.files.upload;

import java.io.InputStream;

public class ChunkFile {
    private int offset;
    private int sizeChunk;
    private InputStream inputStream;

    protected ChunkFile(int offset, int sizeChunk, InputStream inputStream) {
        this.offset = offset;
        this.sizeChunk = sizeChunk;
        this.inputStream = inputStream;
    }

    public int getOffset() {
        return offset;
    }

    public int getSizeChunk() {
        return sizeChunk;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
