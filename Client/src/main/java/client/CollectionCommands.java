package client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Scanner;

@ShellComponent("Collection Commands")
public class CollectionCommands {
    @Autowired
    private User user;
    @Autowired
    private ObjectMapper objectMapper;
    @ShellMethod("Create a Collection")
    public ObjectNode createCollection(String dbName, String collectionName) throws JsonProcessingException {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/create/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        System.out.println("Do you want to include a schema? (y/n)");
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine();
        StringBuilder schema = new StringBuilder();
        Mono<ObjectNode> responseMono;
        if (answer.equals("y")){
            System.out.println("Enter the schema:");
            while (true) {
                String line = sc.nextLine();

                // Check if the line is empty (i.e., just Enter was pressed)
                if (line.isEmpty()) {
                    break; // Exit the loop if an empty line is encountered
                }

                // Append the line to the StringBuilder
                schema.append(line).append("\n");
            }
            responseMono = webClient
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.readTree(schema.toString()))
                    .retrieve()
                    .bodyToMono(ObjectNode.class);
        }else{
            responseMono = webClient
                    .post()
                    .retrieve()
                    .bodyToMono(ObjectNode.class);
        }
        return responseMono.block();
    }
    @ShellMethod("Delete a Collection")
    public ObjectNode deleteCollection(String dbName, String collectionName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/delete/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }

    @ShellMethod("Get all collections")
    public ObjectNode getCollections(String dbName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/getCollections/"+dbName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Get a schema")
    public String getSchema(String dbName, String collectionName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/collection/getSchema/"+dbName+"/"+collectionName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block().toPrettyString();
    }
}
