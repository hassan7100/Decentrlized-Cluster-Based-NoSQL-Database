package client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ShellComponent("Database Commands")
public class DatabaseCommands {
    @Autowired
    private User user;
    @ShellMethod("Create a database")
    public ObjectNode createDatabase(String dbName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/create/"+dbName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Delete a database")
    public ObjectNode deleteDatabase(String dbName) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/delete/"+dbName)
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }
    @ShellMethod("Get all databases")
    public ObjectNode getDatabases() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/database/getdatabases")
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<ObjectNode> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(ObjectNode.class);

        return responseMono.block();
    }

    @ShellMethod("See Logs")
    public String getLogs() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:"+user.getNodeAddress()+"/RequestLogging")
                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
                .build();
        Mono<String> responseMono = webClient
                .get()
                .retrieve()
                .bodyToMono(String.class);
        return responseMono.block();
    }
}
