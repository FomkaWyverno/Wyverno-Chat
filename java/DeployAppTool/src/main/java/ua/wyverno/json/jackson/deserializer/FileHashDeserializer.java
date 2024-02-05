package ua.wyverno.json.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ua.wyverno.files.hashs.FileHash;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHashDeserializer extends StdDeserializer<FileHash> {

    protected FileHashDeserializer() {
        super(FileHash.class);
    }
    protected FileHashDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public FileHash deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode pathNode = node.path("pathFile");
        JsonNode absolutePathNode = node.path("absolutePath");
        JsonNode hashNode = node.path("hash");

        if (pathNode.isMissingNode() || hashNode.isMissingNode() || absolutePathNode.isMissingNode()) {
            throw new IOException("Invalid JSON Structure for FileHash deserialization");
        }

        Path path = Paths.get(pathNode.asText());
        Path absolutePath = Paths.get(absolutePathNode.asText());
        String hash = hashNode.asText();

        return new FileHash(path, absolutePath, hash);
    }
}
