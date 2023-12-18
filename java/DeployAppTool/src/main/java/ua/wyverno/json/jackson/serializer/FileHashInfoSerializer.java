package ua.wyverno.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ua.wyverno.files.hashs.FileHashInfo;

import java.io.IOException;
import java.nio.file.Path;

public class FileHashInfoSerializer extends StdSerializer<FileHashInfo> {

    protected FileHashInfoSerializer() {
        super(FileHashInfo.class);
    }

    @Override
    public void serialize(FileHashInfo fileHashInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("pathFile",fileHashInfo.getPathFile().toString());
        jsonGenerator.writeNumberField("hash", fileHashInfo.getHash());
        jsonGenerator.writeEndObject();
    }
}
