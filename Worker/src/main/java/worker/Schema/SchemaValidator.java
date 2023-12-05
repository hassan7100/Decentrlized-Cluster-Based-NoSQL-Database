package worker.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.util.HashMap;
import java.util.Map;

public class SchemaValidator {
    private final Map<String,JsonNode> schemaMap;
    private static SchemaValidator schemaValidator;
    private SchemaValidator(){
        schemaMap = new HashMap<>();
    }
    public static SchemaValidator getInstance(){
        if (schemaValidator == null)
            schemaValidator = new SchemaValidator();
        return new SchemaValidator();
    }
    public void createSchema(String databaseName, String collectionName,JsonNode schema){
        schemaMap.put(databaseName+"/"+collectionName, schema);
    }
    public boolean isValid(String databaseName, String collectionName,JsonNode jsonDocument){
        try {
            if (schemaMap.containsKey(databaseName + "/" + collectionName)) {
                JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
                JsonSchema jsonSchema = schemaFactory.getJsonSchema(schemaMap.get(databaseName + "/" + collectionName));
                return jsonSchema.validInstance(jsonDocument);
            } else {
                return true;
            }
        }
        catch (ProcessingException e){
            return false;
        }
    }
    public JsonNode getSchema(String databaseName, String collectionName) {
        return schemaMap.get(databaseName+"/"+collectionName);
    }
}
