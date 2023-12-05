package worker.Controller;

import com.fasterxml.jackson.databind.JsonNode;
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
@Slf4j
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private QueryHandlerFactory queryHandlerFactory;
    @PostMapping("/addField/{dbName}/{collectionName}/{index}")
    public ResponseEntity<Status> addField(@PathVariable String dbName, @PathVariable String collectionName, @PathVariable int index, @RequestBody JsonNode field, Authentication authentication){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.AddField)
                .collectionName(collectionName)
                .index(index)
                .fieldName(field.fieldNames().next())
                .fieldValue(field.fields().next().getValue())
                .username(authentication.getName())
                .build();
        Status status = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    @GetMapping("/deleteField/{dbName}/{collectionName}/{index}/{field}")
    public ResponseEntity<Status> removeField(@PathVariable String dbName,@PathVariable String collectionName, @PathVariable int index, @PathVariable String field, Authentication authentication){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.DeleteField)
                .collectionName(collectionName)
                .index(index)
                .fieldName(field)
                .username(authentication.getName())
                .build();
        Status status = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }
    @PostMapping("/updateField/{dbName}/{collectionName}/{index}")
    public ResponseEntity<Status> updateField(@PathVariable String dbName,@PathVariable String collectionName, @PathVariable int index, @RequestBody JsonNode json, Authentication authentication){
        Query query = Query.builder()
                .databaseName(dbName)
                .queryType(QueryType.UpdateField)
                .collectionName(collectionName)
                .index(index)
                .fieldName(json.fieldNames().next())
                .fieldValue(json.fields().next().getValue())
                .username(authentication.getName())
                .build();
        Status status = (Status) queryHandlerFactory.getQueryHandler(query).handleQuery(query);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
