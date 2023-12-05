package worker.Query.CollectionHandler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.Query.Query;
import worker.Service.CollectionService;

@Slf4j
@Component
public class FindDocuments implements CollectionHandler {
    @Autowired
    private CollectionService collectionService;
    public Object handleQuery(Query query) {
        return collectionService.findDocuments(query.getDatabaseName(), query.getCollectionName());
    }
}
