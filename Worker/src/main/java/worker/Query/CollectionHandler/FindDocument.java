package worker.Query.CollectionHandler;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.Query.Query;
import worker.Service.CollectionService;

@Slf4j
@Component
public class FindDocument implements CollectionHandler {
    @Autowired
    private CollectionService collectionService;
    public JsonNode handleQuery(Query query) {
        return collectionService.findDocument(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
    }
}
