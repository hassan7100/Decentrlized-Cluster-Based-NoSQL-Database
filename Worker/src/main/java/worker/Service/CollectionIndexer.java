package worker.Service;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.Access.BPlusTree;
import worker.Cache.CacheManager;
import worker.Schema.SchemaValidator;

import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.directory.SchemaViolationException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@Component
@NoArgsConstructor
@Slf4j
public class CollectionIndexer {
    @Autowired
    private CacheManager cacheManager;
    private RandomAccessFile file;
    private Hashtable<Integer, String> index;
    private BPlusTree<String, Multimap<String, Integer>> propertyIndexer;
    private Hashtable<Integer,ReadWriteLock> locks;
    private ObjectMapper objectMapper;
    private String database;
    private SchemaValidator schemaValidator;
    private String collectionName;
    public CollectionIndexer(String database, String collectionName) throws IOException {
        if(!new File("./data/Database/"+database).exists()){
            throw new IOException("Database not found");
        }
        if(new File("./data/Database/"+database+"/" + collectionName).exists()){
            throw new IOException("Collection already exists");
        }
        new File("./data/Database/"+database+"/" + collectionName).mkdir();
        index = new Hashtable<>();
        propertyIndexer = new BPlusTree<>();
        objectMapper = new ObjectMapper();
        this.database = database;
        this.collectionName = collectionName;
        this.schemaValidator = SchemaValidator.getInstance();
        this.locks = new Hashtable<>(20);
        this.cacheManager = new CacheManager();

    }
    public void setSchema(JsonNode schema) {
        this.schemaValidator.createSchema(database, collectionName, schema);
    }


    public boolean write(JsonNode jsonDocument) throws KeyAlreadyExistsException, SchemaViolationException{
        int id = 0;
        if(jsonDocument.has("_id")) {
            if (jsonDocument.get("_id").isInt()) {
                id = jsonDocument.get("_id").asInt();
            } else if (jsonDocument.get("_id").isTextual()) {
                id = Math.abs(jsonDocument.get("_id").asText().hashCode());
            }
        }
        else {
            id = Math.abs(jsonDocument.hashCode());
            ObjectNode node = objectMapper.createObjectNode();
            node.put("_id",id);
            node.setAll((ObjectNode) jsonDocument);
            jsonDocument = node;

        }
        try {
            locks.put(id, new ReentrantReadWriteLock());
            locks.get(id).writeLock().lock();
        if(index.containsKey(id)){
            throw new KeyAlreadyExistsException("Key already exists");
        }
        int finalId = id;
        if (schemaValidator.getSchema(database,collectionName) != null) {
            if (!schemaValidator.isValid(database,collectionName,jsonDocument)) {
                    throw new SchemaViolationException("Schema Violation");
            }
        }

        jsonDocument.fields().forEachRemaining(entry -> {
            if(propertyIndexer.search(entry.getKey()) == null)
                propertyIndexer.insert(entry.getKey(), Multimaps.synchronizedSetMultimap(HashMultimap.create()));
            try {
                propertyIndexer.search(entry.getKey()).put(objectMapper.writeValueAsString(entry.getValue()), finalId);
            } catch (JsonProcessingException e) {
                    log.error("Error while indexing property: "+entry.getKey()+" on "+database+"/"+collectionName+"/"+finalId);
            }
            });
            if (new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat").createNewFile()) {
                    objectMapper.writeValue(new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat"), jsonDocument);
                    index.put(finalId, "./data/Database/" + database + "/" + collectionName + "/" + id + ".dat");
                    return true;
            } else {
                    return false;
            }

        }
        catch (IOException e){
            return false;
        }
        finally {
            locks.get(id).writeLock().unlock();
        }
    }

    public JsonNode read(int id) throws IOException{
            if(!index.containsKey(id)){
                throw new IOException("Document not found");
            }
            JsonNode jsonNode;
            try {
                locks.get(id).readLock().lock();
                 jsonNode = cacheManager.getJsonObjectFromFile(index.get(id));
            }
            catch (Exception e){
                throw new IOException("Document not found");
            }
            finally {
                locks.get(id).readLock().unlock();
            }

            return jsonNode;
    }

    public boolean deleteById(int id) throws KeyException{
            if (!index.containsKey(id)) {
                throw new KeyException("Key not found");
            }
            try {
                locks.get(id).writeLock().lock();
                JsonNode jsonNode = objectMapper.readTree(new File(index.get(id)));
                jsonNode.fields().forEachRemaining(entry -> {
                    propertyIndexer.search(entry.getKey()).remove(entry.getValue().asText(), id);
                });
                cacheManager.cacheDelete(index.get(id));
                index.remove(id);
                new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat").delete();
                return true;
            }
         catch (IOException e) {
            return false;
        }
        finally {
            locks.get(id).writeLock().unlock();
        }
    }
    public List<JsonNode> readAll() {
        List<JsonNode> result = new ArrayList<>();
        try {
            for(int id : index.keySet()){
                locks.get(id).readLock().lock();
            }
            for (int id : index.keySet()) {
                try {
                    result.add(read(id));
                } catch (IOException exception) {
                    log.error("Error While Reading Document on" + database + "/" + collectionName + "/" + id);
                }
            }
        }
        finally {
            for(int id : index.keySet()){
                locks.get(id).readLock().unlock();
            }
        }
        return result;

    }
    public boolean deleteAll(){
        try {
            for(int id : index.keySet()){
                locks.get(id).writeLock().lock();
            }
            for (int id : index.keySet()) {
                try {
                    deleteById(id);
                } catch (KeyException e) {
                    log.error("Error While Deleting Document on" + database + "/" + collectionName + "/" + id);
                }
            }
        }finally {
            for(int id : index.keySet()){
                locks.get(id).writeLock().unlock();
            }
        }
        return true;
    }
    public List<JsonNode> findBy(String propertyName, JsonNode propertyValue){
        List<Integer> ids;
        List<JsonNode> jsonNodes = new ArrayList<>();
        try {
            ids = propertyIndexer.search(propertyName).get(objectMapper.writeValueAsString(propertyValue)).stream().toList();
        }
        catch (Exception e){
            return new ArrayList<>();
        }
        try {
            for(int id : ids){
                locks.get(id).readLock().lock();
            }
            for (int id : ids) {
                try {
                    jsonNodes.add(read(id));
                } catch (IOException ignored) {
                    log.error("Error While Reading Document on" + database + "/" + collectionName + "/" + id);
                }
            }
        }finally {
            for(int id : ids){
                locks.get(id).readLock().unlock();
            }
        }
        return jsonNodes;
    }
    public boolean deleteField (int id, String fieldName) throws IOException {
        if (!index.containsKey(id)) {
            throw new IOException("Document not found");
        }
        try {
            if (!propertyIndexer.search(fieldName).containsEntry(objectMapper.writeValueAsString(read(id).get(fieldName)), id)) {
                throw new IOException("Field not found");
            }
        }
        catch (Exception e){
            throw new IOException("Field not found");
        }
        try {
            locks.get(id).writeLock().lock();
            JsonNode jsonNode = objectMapper.readTree(new File(index.get(id)));
            String string = jsonNode.get(fieldName).asText();
            ((ObjectNode) jsonNode).remove(fieldName);
            propertyIndexer.search(fieldName).remove(string, id);
            objectMapper.writeValue(new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat"), jsonNode);
            cacheManager.cacheRefresh(index.get(id), jsonNode);
        }finally {
            locks.get(id).writeLock().unlock();
        }
        return true;
    }
    public boolean updateField (int id, String fieldName, JsonNode fieldValue) throws IOException {
        String s = objectMapper.writeValueAsString(objectMapper.readTree(new File(index.get(id))).findValue(fieldName));
        if (!index.containsKey(id)) {
            throw new IOException("Document not found");
        }
        if(!propertyIndexer.search(fieldName).containsEntry(s, id)){
            throw new IOException("Field not found");
        }
        try {
            locks.get(id).writeLock().lock();
            JsonNode jsonNode = objectMapper.readTree(new File(index.get(id)));
            propertyIndexer.search(fieldName).remove(s, id);
            ((ObjectNode) jsonNode).put(fieldName, fieldValue);
            propertyIndexer.search(fieldName).put(objectMapper.writeValueAsString(fieldValue), id);
            objectMapper.writeValue(new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat"), jsonNode);
            cacheManager.cacheRefresh(index.get(id), jsonNode);
        }finally {
            locks.get(id).writeLock().unlock();
        }
        return true;
    }
    public boolean addField (int id, String fieldName, JsonNode fieldValue) throws IOException {
        if (!index.containsKey(id) ) {
            throw new IOException("Document not found");
        }
        if(propertyIndexer.search(fieldName) == null){
            propertyIndexer.insert(fieldName, HashMultimap.create());
        }
        else if (propertyIndexer.search(fieldName).containsEntry(objectMapper.writeValueAsString(read(id).get(fieldName)), id)){
                throw new IOException("Field already exists");
        }
        try {
            locks.get(id).writeLock().lock();
            JsonNode jsonNode = objectMapper.readTree(new File(index.get(id)));
            ((ObjectNode) jsonNode).put(fieldName, fieldValue);
            propertyIndexer.search(fieldName).put(objectMapper.writeValueAsString(fieldValue), id);
            objectMapper.writeValue(new File("./data/Database/" + database + "/" + collectionName + "/" + id + ".dat"), jsonNode);
            cacheManager.cacheRefresh(index.get(id), jsonNode);
        }finally {
            locks.get(id).writeLock().unlock();
        }
        return true;
    }
    public JsonNode getSchema() {
        return schemaValidator.getSchema(database,collectionName) == null ? objectMapper.createObjectNode()
                .put("empty","true") : schemaValidator.getSchema(database,collectionName);
    }

}
