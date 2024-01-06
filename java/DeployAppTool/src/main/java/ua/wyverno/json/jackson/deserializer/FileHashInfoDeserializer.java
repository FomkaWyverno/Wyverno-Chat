package ua.wyverno.json.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ua.wyverno.files.hashs.FileHashInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHashInfoDeserializer extends StdDeserializer<FileHashInfo> {

    protected FileHashInfoDeserializer() {
        super(FileHashInfo.class);
    }
    protected FileHashInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public FileHashInfo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode pathNode = node.path("pathFile");
        JsonNode hashNode = node.path("hash");

        if (pathNode.isMissingNode() || hashNode.isMissingNode()) {
            throw new IOException("Invalid JSON Structure for FileHashInfo deserialization");
        }

        Path path = Paths.get(pathNode.asText());
        String hash = hashNode.asText();

        return new FileHashInfo(path, hash);
    }
}
