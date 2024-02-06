package ua.wyverno.files.hashs;

import java.io.IOException;

public interface Hashing {
    void calculateChecksum() throws IOException;
    String getHash();
}
