package worker.Service;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.Access.BPlusTree;
import worker.Status;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class DatabaseService {
    private final BPlusTree<String, CollectionIndexer> collections;
    @Autowired
    public DatabaseService(BPlusTree<String, CollectionIndexer> collections) {
        new File("./data/Database").mkdir();
        this.collections = collections;
    }
    public Status createDatabase(String databaseName) {
        return new File("./data/Database/" + databaseName).mkdir() ?
                new Status(Status.StatusType.Success, "Database created successfully") :
                new Status(Status.StatusType.Failure, "Database already exists");
    }
    public Status dropDatabase(String databaseName) {
        try {
            for(String collectionName : new File("./data/Database/" + databaseName).list()) {
                FileUtils.deleteDirectory(new File("./data/Database/" + databaseName + "/" + collectionName));
                collections.delete(databaseName + collectionName.replace(".dat", ""));
            }
            boolean isDeleted = new File("./data/Database/" + databaseName).delete();
            return isDeleted ?
                    new Status(Status.StatusType.Success, "Database deleted successfully") :
                    new Status(Status.StatusType.Failure, "Database not found");
        }
        catch (Exception e) {
            return new Status(Status.StatusType.Failure, "Database not found");
        }
    }
    public Status createCollection(String databaseName, String collectionName, JsonNode schema) {
        try {
            CollectionIndexer collectionIndexer = new CollectionIndexer(databaseName, collectionName);
            if(schema != null && !schema.isNull()){
                collectionIndexer.setSchema(schema);
            }
            collections.insert(databaseName + collectionName, collectionIndexer);
            return new Status(Status.StatusType.Success, "Collection created successfully");
        }
        catch (IOException e) {
            return new Status(Status.StatusType.Failure, e.getMessage());
        }
    }
    public Status dropCollection(String databaseName, String collectionName) {
        try {
            if(new File("./data/Database/" + databaseName + "/" + collectionName).exists()){
                FileUtils.deleteDirectory(new File("./data/Database/" + databaseName + "/" + collectionName));
                collections.search(databaseName + collectionName).deleteAll();
                
                collections.delete(databaseName + collectionName);
                return new Status(Status.StatusType.Success, "Collection deleted successfully");
            }
            else{
                return new Status(Status.StatusType.Failure, "Collection not found");
            }
        } catch (Exception e) {
            return new Status(Status.StatusType.Failure, "Collection not found");
        }
    }
    public Status getDatabases() {
        return new Status(Status.StatusType.Success, String.join(",", new File("./data/Database").list()));
    }
    public Status getCollections(String databaseName) {
        return new Status(Status.StatusType.Success, String.join(",", new File("./data/Database/" + databaseName).list()));
    }



}
