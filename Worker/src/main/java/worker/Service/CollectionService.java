package worker.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.Access.BPlusTree;
import worker.Status;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionService {
    private final BPlusTree<String, CollectionIndexer> collections;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public CollectionService(BPlusTree<String, CollectionIndexer> collections) {
        this.collections = collections;
    }

    public Status addDocument(String databaseName, String collectionName, JsonNode jsonDocument) {
        try {
            boolean isExecuted = collections.search(databaseName + collectionName).write(jsonDocument);
            if (isExecuted) {
                return new Status(Status.StatusType.Success, "Document added successfully");
            } else {
                return new Status(Status.StatusType.Failure, "Document could not be added");
            }
        } catch (KeyAlreadyExistsException |SchemaViolationException| NullPointerException exception) {
            if (exception instanceof NullPointerException)
                return new Status(Status.StatusType.Failure, "Collection not found");
            else if(exception instanceof SchemaViolationException)
                return new Status(Status.StatusType.Failure, "Schema Violation");
            else
                return new Status(Status.StatusType.Failure, exception.getMessage());
        }
    }

    public Status removeDocument(String databaseName, String collectionName, int id) {
        try {
            if (collections.search(databaseName + collectionName).deleteById(id)) {
                return new Status(Status.StatusType.Success, "Document deleted successfully");
            } else {
                return new Status(Status.StatusType.Failure, "Document not found");
            }
        } catch (KeyException e) {
            return new Status(Status.StatusType.Failure, e.getMessage());
        } catch (NullPointerException e) {
            return new Status(Status.StatusType.Failure, "Collection or Database not found");
        }
    }

    public List<JsonNode> getDocumentsByField(String databaseName, String collectionName, String key, JsonNode value) {
        try {
            return collections.search(databaseName + collectionName).findBy(key, value);
        }
        catch (NullPointerException e){
            return new ArrayList<>();
        }
    }

    public JsonNode findDocument(String databaseName, String collectionName, int id) {
        try {
            JsonNode jsonNode = collections.search(databaseName + collectionName).read(id);
            if (jsonNode != null) {
                return jsonNode;
            } else {
                return objectMapper.createObjectNode()
                        .put("statusType", "Failure")
                        .put("message", "Document not found");
            }
        } catch (NullPointerException e) {
            return objectMapper.createObjectNode()
                    .put("statusType", "Failure")
                    .put("message", "Collection or Database not found");
        }
        catch (IOException e){
            return objectMapper.createObjectNode()
                    .put("statusType", "Failure")
                    .put("message", e.getMessage());
        }
    }

    public List<JsonNode> findDocuments(String databaseName, String collectionName) {
        try {
            return collections.search(databaseName + collectionName).readAll();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public JsonNode findDocumentAsJson(String databaseName, String collectionName, int id) {
        try {
            JsonNode jsonNode = collections.search(databaseName + collectionName).read(id);
            if (jsonNode != null) {
                return jsonNode;
            } else {
                return objectMapper.createObjectNode();
            }
        } catch (IOException | NullPointerException e) {
            return objectMapper.createObjectNode();
        }
    }

    public JsonNode getSchema(String databaseName, String collectionName) {
        try {
            System.out.println(collections.search(databaseName + collectionName).getSchema().toString());
            return collections.search(databaseName + collectionName).getSchema();
        }catch (NullPointerException e){
            return objectMapper.createObjectNode()
                    .put("statusType", "Failure")
                    .put("message", "Collection or Database not found");
        }
    }
}