package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ShellComponent("Document Commands")
public class DocumentCommands {
    @Autowired
    private User user;
    @ShellMethod("Add a document" )
    public ObjectNode addDocument(String dbName, String collectionName, String document) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + user.getNodeAddress() + "/collection/create/" + dbName + "/" + collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode documentNode;
        try {
            documentNode = objectMapper.readTree(document);
        } catch (JsonProcessingException e) {
            return  objectMapper.createObjectNode()
                    .put("statusType", "Failed")
                    .put("message", "Invalid JSON");
        }
        Mono<ObjectNode> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(documentNode)
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Delete a document")
    public ObjectNode deleteDocument(String dbName, String collectionName, int index) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/delete/"+dbName+"/"+collectionName+"/"+index)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Get all documents")
    public String getDocuments(String dbName, String collectionName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/find/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<List<JsonNode>> responseMono = webClient
                .get()
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        StringBuilder stringBuilder = new StringBuilder();
        for (JsonNode jsonNode : responseMono.block()) {
            try {
                stringBuilder.append(objectMapper.writeValueAsString(jsonNode)).append("\n");
            } catch (JsonProcessingException ignored) {
            }
        }
        return stringBuilder.toString();
    }
    @ShellMethod("Add a field to a document")
    public ObjectNode addField(String dbName, String collectionName, int index, String field, String value) throws JsonProcessingException {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/document/addField/"+dbName+"/"+collectionName+"/"+index)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.nullNode();
        try {
            jsonNode = objectMapper.readTree(value);
        }
        catch (JsonProcessingException e){

        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (jsonNode.isNull())
            objectNode.put(field,value);
        else
            objectNode.put(field,jsonNode);
        Mono<ObjectNode> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Delete a field from a document")
    public ObjectNode deleteField(String dbName, String collectionName, int index,String field) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/document/deleteField/"+dbName+"/"+collectionName+"/"+index+"/"+field)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Update a field in a document")
    public ObjectNode updateField(String dbName, String collectionName, int index,String field,String value) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/document/updateField/"+dbName+"/"+collectionName+"/"+index)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(value);
        }
        catch (JsonProcessingException e){
            jsonNode = objectMapper.nullNode();
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (jsonNode.isNull())
            objectNode.put(field,value);
        else
            objectNode.put(field,jsonNode);
        Mono<ObjectNode> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Get a document by field")
    public String getDocumentByField(String dbName, String collectionName, String field,String value) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/findByFilter/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(value);
        }
        catch (JsonProcessingException e){
            jsonNode = objectMapper.nullNode();
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (jsonNode.isNull())
            objectNode.put(field,value);
        else
            objectNode.put(field,jsonNode);
        Mono<List<JsonNode>> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectNode)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        StringBuilder stringBuilder = new StringBuilder();
        for (JsonNode jsonNode1 : responseMono.block()) {
            try {
                stringBuilder.append(objectMapper.writeValueAsString(jsonNode1)+"\n");
            } catch (Exception ignored) {

            }
        }
        return "[\n"+stringBuilder+"]";
    }
    @ShellMethod("Get documents by field")
    public String findByMultiFilter(String dbName, String collectionName, String field,String value) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/findByMultiFilter/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(value);
        }
        catch (JsonProcessingException e){
            jsonNode = objectMapper.nullNode();
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (jsonNode.isNull())
            objectNode.put(field,value);
        else
            objectNode.put(field,jsonNode);
        Mono<List<JsonNode>> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectNode)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        StringBuilder stringBuilder = new StringBuilder();
        for (JsonNode jsonNode1 : responseMono.block()) {
            try {
                stringBuilder.append(objectMapper.writeValueAsString(jsonNode1)+"\n");
            } catch (Exception ignored) {

            }
        }
        return "[\n"+stringBuilder+"]";
    }
    @ShellMethod("Get documents by field")
    public String getDocumentsByMultiFilter(String databaseName, String collectionName,String Filter) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/findBymultipleFilters/"+databaseName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(Filter);
        }
        catch (JsonProcessingException e){
            return "Invalid JSON";
        }
        Mono<List<JsonNode>> responseMono = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonNode)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        StringBuilder stringBuilder = new StringBuilder();
        for (JsonNode jsonNode1 : responseMono.block()) {
            try {
                stringBuilder.append(objectMapper.writeValueAsString(jsonNode1)+"\n");
            } catch (Exception ignored) {

            }
        }
        return "[\n"+stringBuilder+"]";
    }
    @ShellMethod("Get a document by index")
    public String getDocumentByIndex(String dbName, String collectionName, int index) throws JsonProcessingException {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/find/"+dbName+"/"+collectionName+"/"+index)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<JsonNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(JsonNode.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(responseMono.block());
    }
}
