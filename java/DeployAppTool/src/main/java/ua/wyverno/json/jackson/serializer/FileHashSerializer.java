package ua.wyverno.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ua.wyverno.files.hashs.FileMetadataNode;

import java.io.IOException;

public class FileHashSerializer extends StdSerializer<FileMetadataNode> {

    protected FileHashSerializer() {
        super(FileMetadataNode.class);
    }

    @Override
    public void serialize(FileMetadataNode fileHash, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("pathFile", fileHash.toPath().toString());
        //jsonGenerator.writeStringField("absolutePath", fileHash.getAbsolutePath().toString());
        jsonGenerator.writeStringField("hash", fileHash.getContentHash());
        jsonGenerator.writeEndObject();
    }
}
