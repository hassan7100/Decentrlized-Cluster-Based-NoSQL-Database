package worker.Service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.Access.BPlusTree;
import worker.Status;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class DocumentService {
    private final BPlusTree<String, CollectionIndexer> collections;
    @Autowired
    public DocumentService(BPlusTree<String, CollectionIndexer> collections) {
        this.collections = collections;
    }
    public Status addField(String databaseName, String collectionName, Integer index, String fieldName, JsonNode fieldValue){
        try {
            boolean isExecuted = collections.search(databaseName+collectionName).addField(index,fieldName,fieldValue);
            if(isExecuted){
                return new Status(Status.StatusType.Success, "Field added successfully");
            }
            else{
                return new Status(Status.StatusType.Failure, "Field could not be added");
            }
        }catch (IOException e){
            return new Status(Status.StatusType.Failure, e.getMessage());
        }
        catch (NullPointerException e){
            return new Status(Status.StatusType.Failure, "Collection or Database not found");
        }
    }
    public Status removeField(String databaseName, String collectionName,Integer index, String fieldName){
        try {
            boolean isExecuted = collections.search(databaseName+collectionName).deleteField(index,fieldName);
            if(isExecuted){
                return new Status(Status.StatusType.Success, "Field deleted successfully");
            }
            else{
                return new Status(Status.StatusType.Failure, "Field could not be deleted");
            }
        }catch (IOException e){
            return new Status(Status.StatusType.Failure, e.getMessage());
        }
        catch (NullPointerException e){
            return new Status(Status.StatusType.Failure, "Collection or Database not found");
        }
    }
    public Status updateField(String databaseName, String collectionName,Integer index, String fieldName, JsonNode fieldValue){
        try {
            boolean isExecuted = collections.search(databaseName+collectionName).updateField(index,fieldName,fieldValue);
            if(isExecuted){
                return new Status(Status.StatusType.Success, "Field updated successfully");
            }
            else{
                return new Status(Status.StatusType.Failure, "Field could not be updated");
            }
        }catch (IOException e){
            return new Status(Status.StatusType.Failure, e.getMessage());
        }
        catch (NullPointerException e){
            return new Status(Status.StatusType.Failure, "Collection or Database not found");
        }
    }
}


