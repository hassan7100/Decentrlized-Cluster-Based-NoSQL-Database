package worker.Query.CollectionHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.Query.Query;
import worker.Service.CollectionService;


@Service
public class GetDocumentsByMultipleFields implements CollectionHandler{
    @Autowired
    private CollectionService collectionService;
    public Object handleQuery(Query query) {
        return collectionService.getDocumentsByField(query.getDatabaseName(), query.getCollectionName(), query.getFieldName(), query.getFieldValue());
    }
}
