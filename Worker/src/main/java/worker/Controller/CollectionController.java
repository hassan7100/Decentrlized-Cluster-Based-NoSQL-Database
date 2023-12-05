package worker.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.main.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import worker.AffinityCalculator;
import worker.ClusterComm.BroadcastSender;
import worker.ClusterComm.RequestWrite;
import worker.Query.Query;
import worker.Query.QueryHandler;
import worker.Query.QueryHandlerFactory;
import worker.Query.QueryType;
import worker.Status;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    private QueryHandlerFactory queryHandlerFactory;
    public void updateSchema(JsonNode jsonNode){
    }
    @PostMapping("/create/{dbName}/{collectionName}")
    public ResponseEntity<Status> createDocument(@PathVariable String dbName, @PathVariable String collectionName, @RequestBody JsonNode document, Authentication authentication) {
        Query query;
        query = Query.builder()
                    .databaseName(dbName)
                    .queryType(QueryType.AddDocument)
                    .collectionName(collectionName)
                    .document(document)
                    .username(authentication.getName())
                    .build();
        Status status = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        log.info("User: "+" tried to create a document in collection: "+dbName+"/"+collectionName+", status: "+status.getStatusType());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    @GetMapping("/delete/{dbName}/{collectionName}/{index}")
    public ResponseEntity<Status> deleteDocument(@PathVariable String dbName, @PathVariable String collectionName, @PathVariable int index,Authentication authentication){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.DeleteDocument)
                .collectionName(collectionName)
                .index(index)
                .username(authentication.getName())
                .build();
        Status status = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        log.info("User: "+" tried to delete a document in collection: "+dbName+"/"+collectionName+", status: "+status.getStatusType());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    @PostMapping("/findByFilter/{dbName}/{collectionName}")
    public List<JsonNode> findByFilter(@PathVariable String dbName, @PathVariable String collectionName,@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.FindDocumentsByFilter)
                .collectionName(collectionName)
                .fieldName(jsonNode.fieldNames().next())
                .fieldValue(jsonNode.fields().next().getValue())
                .build();
        return (List<JsonNode>) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
    }
    @PostMapping("/findBymultipleFilters/{dbName}/{collectionName}")
    public List<JsonNode> findByMultipleFilters(@PathVariable String dbName, @PathVariable String collectionName,@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.FindDocumentsByMultipleFilters)
                .collectionName(collectionName)
                .fieldName(jsonNode.fieldNames().next())
                .fieldValue(jsonNode.fields().next().getValue())
                .build();
        return (List<JsonNode>) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
    }
    @GetMapping("/find/{dbName}/{collectionName}/{index}")
    public JsonNode findDocument(@PathVariable String dbName, @PathVariable String collectionName, @PathVariable int index){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.FindDocument)
                .collectionName(collectionName)
                .index(index)
                .build();

        return (JsonNode) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
    }
    @GetMapping("/find/{dbName}/{collectionName}")
    public List<JsonNode> findDocuments(@PathVariable String dbName, @PathVariable String collectionName){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.FindDocuments)
                .collectionName(collectionName)
                .build();
        return (List<JsonNode>) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
    }
    @GetMapping("/getSchema/{dbName}/{collectionName}")
    public JsonNode findSchema(@PathVariable String dbName, @PathVariable String collectionName){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.GetSchema)
                .collectionName(collectionName)
                .build();
        return (JsonNode)queryHandlerFactory.getQueryHandler(query).handleQuery(query);
    }

}
