package ua.wyverno.json.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ua.wyverno.dropbox.metadata.FolderMetadata;

import java.io.IOException;
import java.util.Objects;

public class FolderMetadataDeserializer extends StdDeserializer<FolderMetadata> {
    protected FolderMetadataDeserializer() {
        super(FolderMetadata.class);
    }

    @Override
    public FolderMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode nameNode = node.path("name");
        JsonNode idNode = node.path("id");
        JsonNode pathLowerNode = node.path("path_lower");
        JsonNode pathDisplayNode = node.path("path_display");

        if (nameNode.isMissingNode() || idNode.isMissingNode()) throw new IOException("Invalid JSON Structure for FolderMetadata deserialization");

        return new FolderMetadata.Builder()
                .name(Objects.requireNonNull(nameNode.asText()))
                .id(Objects.requireNonNull(idNode.asText()))
                .pathLower(pathLowerNode.asText())
                .pathDisplay(pathDisplayNode.asText())
                .createFolderMetadata();
    }
}
