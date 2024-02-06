package ua.wyverno.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ua.wyverno.files.hashs.FileHash;

import java.io.IOException;

public class FileHashSerializer extends StdSerializer<FileHash> {

    protected FileHashSerializer() {
        super(FileHash.class);
    }

    @Override
    public void serialize(FileHash fileHash, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("pathFile", fileHash.getRelativePath().toString());
        jsonGenerator.writeStringField("absolutePath", fileHash.getAbsolutePath().toString());
        jsonGenerator.writeStringField("hash", fileHash.getHash());
        jsonGenerator.writeEndObject();
    }
}
