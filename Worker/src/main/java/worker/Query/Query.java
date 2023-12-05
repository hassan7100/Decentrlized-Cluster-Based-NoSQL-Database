package worker.Query;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Query {
    private QueryType queryType;
    private String databaseName;
    private String collectionName;
    private Integer documentId;
    private JsonNode document;
    private Integer index;
    private String fieldName;
    private JsonNode OldValue;
    private JsonNode fieldValue;
    private String field;
    private String value;
    private JsonNode schema;
    private String username;

    @Builder.Default
    private Boolean broadcastMessage = false;
}
