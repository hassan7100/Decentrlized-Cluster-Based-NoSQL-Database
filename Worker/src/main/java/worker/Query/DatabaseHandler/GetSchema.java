package worker.Query.DatabaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.Query.Query;
import worker.Service.CollectionService;

@Component
public class GetSchema implements DatabaseHandler {
    @Autowired
    private CollectionService collectionService;
    @Override
    public Object handleQuery(Query query) {
        return collectionService.getSchema(query.getDatabaseName(), query.getCollectionName());
    }
}
