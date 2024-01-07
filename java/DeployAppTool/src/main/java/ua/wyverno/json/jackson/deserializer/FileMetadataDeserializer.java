package ua.wyverno.json.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ua.wyverno.dropbox.metadata.FileMetadata;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileMetadataDeserializer extends StdDeserializer<FileMetadata> {
    protected FileMetadataDeserializer() {
        super(FileMetadata.class);
    }

    @Override
    public FileMetadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode nameNode = node.path("name");
        JsonNode idNode = node.path("id");
        JsonNode clientModifiedNode = node.path("client_modified");
        JsonNode serverModifiedNode = node.path("server_modified");
        JsonNode revNode = node.path("rev");
        JsonNode sizeNode = node.path("size");
        JsonNode pathLowerNode = node.path("path_lower");
        JsonNode pathDisplayNode = node.path("path_display");
        JsonNode isDownloadableNode = node.path("is_downloadable");
        JsonNode contentHashNode = node.path("content_hash");

        if (nameNode.isMissingNode() || idNode.isMissingNode() || clientModifiedNode.isMissingNode() ||
            serverModifiedNode.isMissingNode() || revNode.isMissingNode() || sizeNode.isMissingNode() ||
            contentHashNode.isMissingNode() || isDownloadableNode.isMissingNode()) {
            throw new IOException("Invalid JSON Structure for FileMetadata deserialization");
        }
        String clientModifiedStr = Objects.requireNonNull(clientModifiedNode.asText());
        String serverModifiedStr = Objects.requireNonNull(serverModifiedNode.asText());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        LocalDateTime clientModified = LocalDateTime.parse(clientModifiedStr, formatter);
        LocalDateTime serverModified = LocalDateTime.parse(serverModifiedStr, formatter);

        return new FileMetadata.Builder()
                .name(Objects.requireNonNull(nameNode.asText()))
                .id(Objects.requireNonNull(idNode.asText()))
                .clientModified(clientModified)
                .serverModified(serverModified)
                .rev(Objects.requireNonNull(revNode.asText()))
                .size(sizeNode.asLong())
                .pathLower(pathLowerNode.asText())
                .pathDisplay(pathDisplayNode.asText())
                .isDownloadable(isDownloadableNode.asBoolean())
                .contentHash(Objects.requireNonNull(contentHashNode.asText()))
                .createFileMetadata();
    }
}
