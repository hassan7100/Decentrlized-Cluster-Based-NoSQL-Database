package client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


//@ShellComponent("Client Commands")
//public class UserCommands  {
//    @Autowired
//    private WebClient.Builder authBuilder;
//    private String username;
//    private String password;
//    private int nodeNumber;
//
//
//
//
//    @ShellMethod("Add a document")
//    public ObjectNode addDocument(String dbName, String collectionName, String document) {
//        WebClient webClient = WebClient.builder()
//                .baseUrl("http://localhost:808" + nodeNumber + "/collection/create/" + dbName + "/" + collectionName)
//                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
//                .build();
//
//        // Convert the document string to a JSON object
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode documentNode;
//        try {
//            documentNode = objectMapper.readTree(document);
//            System.out.println(documentNode.toString()+" "+documentNode.isObject()+"--->");
//        } catch (JsonProcessingException e) {
//            System.out.println("Error parsing JSON: " + e.getMessage());
//            // Handle the exception appropriately
//            e.printStackTrace();
//            return null;
//        }
//
//        Mono<ObjectNode> responseMono = webClient
//                .post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(documentNode)
//                .retrieve()
//                .bodyToMono(ObjectNode.class);
//
//        return responseMono.block();
//    }
//    @ShellMethod("Delete a document")
//    public ObjectNode deleteDocument(String dbName, String collectionName, int index) {
//        WebClient webClient = WebClient.builder().baseUrl("http://localhost:808"+nodeNumber+"/collection/delete/"+dbName+"/"+collectionName+"/"+index)
//                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
//                .build();
//        Mono<ObjectNode> responseMono = webClient
//                .get()
//                .retrieve()
//                .bodyToMono(ObjectNode.class);
//
//        return responseMono.block();
//    }
//    @ShellMethod("Process JSON input")
//    public void processJson(String jsonInput) {
//        // Parse the JSON string into a JSON object
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            JsonNode jsonObject = objectMapper.readTree(jsonInput);
//
//            System.out.println("Parsed JSON: " + jsonObject.toString());
//
//        } catch (JsonProcessingException e) {
//            System.out.println("Error parsing JSON: " + e.getMessage());
//        }
//    }
//}
