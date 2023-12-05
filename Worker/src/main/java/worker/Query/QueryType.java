package worker.Query;

public enum QueryType {
    CreateDatabase,
    DeleteDatabase,
    GetDatabases,
    CreateCollection,
    DeleteCollection,
    GetCollections,
    AddDocument,
    DeleteDocument,
    AddField,
    DeleteField,
    UpdateField,
    FindDocument,
    FindDocuments,
    FindDocumentsByFilter,
    FindDocumentsByMultipleFilters,
    GetSchema
}
