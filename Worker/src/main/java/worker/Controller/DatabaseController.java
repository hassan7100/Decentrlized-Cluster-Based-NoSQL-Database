package worker.Controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import worker.Query.Query;
import worker.Query.QueryHandler;
import worker.Query.QueryHandlerFactory;
import worker.Query.QueryType;
import worker.Status;

@RestController
@RequestMapping("/database")
@Slf4j
public class DatabaseController {
    @Autowired
    private QueryHandlerFactory queryHandlerFactory;
    @GetMapping("/create/{dbName}")
    public ResponseEntity<Status> createDatabase(@PathVariable String dbName, Authentication authentication) {
        Query query = Query.builder()
                .queryType(QueryType.CreateDatabase)
                .databaseName(dbName)
                .username(authentication.getName())
                .build();
        Status isCreated = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(isCreated, HttpStatus.OK);
    }
    @GetMapping("/delete/{dbName}")
    public ResponseEntity<Status> deleteDatabase(@PathVariable String dbName, Authentication authentication) {
        Query query = Query.builder()
                .queryType(QueryType.DeleteDatabase)
                .databaseName(dbName)
                .username(authentication.getName())
                .build();

        Status isDeleted = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(isDeleted, HttpStatus.OK);
    }
    @PostMapping("/create/{dbName}/{collectionName}")
    public ResponseEntity<Status> createCollection(@PathVariable String dbName, @PathVariable String collectionName, @RequestBody(required = false) JsonNode schema,Authentication authentication) {
        Query query = Query.builder()
                .queryType(QueryType.CreateCollection)
                .databaseName(dbName)
                .collectionName(collectionName)
                .schema(schema)
                .username(authentication.getName())
                .build();
        Status isCreated = (Status)queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(isCreated, HttpStatus.OK);
    }
    @GetMapping("/delete/{dbName}/{collectionName}")
    public ResponseEntity<Status> deleteCollection(@PathVariable String dbName, @PathVariable String collectionName, Authentication authentication) {
        Query query = Query.builder()
                .queryType(QueryType.DeleteCollection)
                .databaseName(dbName)
                .collectionName(collectionName)
                .username(authentication.getName())
                .build();
        Status isDeleted = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(isDeleted, HttpStatus.OK);
    }
    @GetMapping("/getdatabases")
    public ResponseEntity<Status> getDatabases() {
        System.out.println("getDatabases");
        Query query = Query.builder()
                .queryType(QueryType.GetDatabases)
                .build();
        Status databases = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(databases, HttpStatus.OK);
    }
    @GetMapping("/getCollections/{dbName}")
    public ResponseEntity<Status> getCollections(@PathVariable String dbName) {
        Query query = Query.builder()
                .queryType(QueryType.GetCollections)
                .databaseName(dbName)
                .build();
        Status collections = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(collections, HttpStatus.OK);
    }


}
