package worker.Cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@Data
public class CacheManager {
    @Autowired
    private ObjectMapper objectMapper;
    private static final int MAX_CACHE_SIZE = 100; // Maximum number of JSON objects to cache
    private static Cache<String, JsonNode> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE) // Maximum number of entries in the cache
            .concurrencyLevel(1000)
            .expireAfterAccess(60, java.util.concurrent.TimeUnit.MINUTES)
            .build();
    public CacheManager() {
        objectMapper = new ObjectMapper();
    }
    public JsonNode getJsonObjectFromFile(String filePath){
            try {
                return cache.get(filePath, () -> readJsonFromFile(filePath));
            } catch (ExecutionException e) {
                return objectMapper.nullNode();
            }
    }
    private JsonNode readJsonFromFile(String filePath){
        try {
            return objectMapper.readTree(new File(filePath));
        }
        catch (IOException e){
            System.out.println("Error reading file: " + filePath);
            return objectMapper.nullNode();
        }
    }
    public void cacheRefresh(String filePath, JsonNode jsonNode) {
        cache.put(filePath, jsonNode);
    }
    public void cacheDelete(String filePath){
        cache.invalidate(filePath);
    }
}
