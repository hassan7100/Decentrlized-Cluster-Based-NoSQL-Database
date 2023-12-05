package worker.Query;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.Query.CollectionHandler.*;
import worker.Query.DatabaseHandler.*;
import worker.Query.DocumentHandler.AddField;
import worker.Query.DocumentHandler.DeleteField;
import worker.Query.DocumentHandler.UpdateField;

import java.util.HashMap;

@Slf4j
@Service
public class QueryHandlerFactory {
    private HashMap<QueryType, QueryHandler> queryHandlerMap;
    @Autowired
    public QueryHandlerFactory(
            GetDatabases getDatabases,
            GetCollections getCollections,
            CreateDatabase createDatabaseHandler,
            CreateCollection createCollection,
            DeleteDatabase deleteDatabase,
            DeleteCollection deleteCollection,
            AddDocument addDocument,
            DeleteDocument deleteDocument,
            FindDocuments findDocuments,
            GetDocumentsByField getDocumentsByField,
            FindDocument findDocument,
            AddField addField,
            DeleteField deleteField,
            UpdateField updateField,
            GetDocumentsByMultipleFields getDocumentsByMultipleFields,
            GetSchema getSchema
    ) {
        queryHandlerMap = new HashMap<>();
        queryHandlerMap.put(QueryType.GetDatabases, getDatabases);
        queryHandlerMap.put(QueryType.GetCollections, getCollections);
        queryHandlerMap.put(QueryType.CreateDatabase, createDatabaseHandler);
        queryHandlerMap.put(QueryType.CreateCollection, createCollection);
        queryHandlerMap.put(QueryType.DeleteDatabase, deleteDatabase);
        queryHandlerMap.put(QueryType.DeleteCollection, deleteCollection);

        queryHandlerMap.put(QueryType.AddDocument, addDocument);
        queryHandlerMap.put(QueryType.DeleteDocument, deleteDocument);
        queryHandlerMap.put(QueryType.FindDocuments, findDocuments);
        queryHandlerMap.put(QueryType.FindDocumentsByFilter, getDocumentsByField);
        queryHandlerMap.put(QueryType.FindDocument, findDocument);
        queryHandlerMap.put(QueryType.GetSchema, getSchema);
        queryHandlerMap.put(QueryType.FindDocumentsByMultipleFilters, getDocumentsByMultipleFields);

        queryHandlerMap.put(QueryType.AddField, addField);
        queryHandlerMap.put(QueryType.DeleteField, deleteField);
        queryHandlerMap.put(QueryType.UpdateField, updateField);
    }
    public QueryHandler getQueryHandler(Query query) {
        return queryHandlerMap.get(query.getQueryType());
    }
    public void addQueryHandler(QueryType queryType, QueryHandler queryHandler) {
        queryHandlerMap.put(queryType, queryHandler);
    }
}
