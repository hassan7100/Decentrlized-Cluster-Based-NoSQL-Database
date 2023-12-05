package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Scanner;

@ShellComponent("Login and Register Commands")
public class LoginAndRegisterCommands extends AbstractShellComponent {
    @Autowired
    private User user;
    @Autowired
    private WebClient.Builder authBuilder;

    @ShellMethod("Contact Bootstrap to login")
    public ObjectNode login() {
        System.out.println("Enter username: ");
        Scanner sc = new Scanner(System.in);
        String username = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        WebClient webClient = authBuilder.baseUrl("http://localhost:8000")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic yourBase64EncodedCredentials")
                .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
                .build();

        ObjectNode responseMono = webClient.get()
                .uri("/returnNodeNumber")
                .retrieve()
                .bodyToMono(ObjectNode.class).block();
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if(responseMono.has("nodeNumber")){
            this.user.setUsername( username);
            this.user.setPassword(password);
            this.user.setNodeAddress(responseMono.get("nodeNumber").asInt());
            return objectNode.put("statusType", "Success");
        }
        else
            return objectNode.put("statusType", "Failed");
    }
    @ShellMethod("Sign up, user mode only")
    public String signUp(String username, String password, String email){
        JsonNode json = new ObjectMapper().createObjectNode()
                .put("username", username)
                .put("password", password)
                .put("email", email);
        WebClient webClient = authBuilder.baseUrl("http://localhost:8000")
                .build();
        String s = webClient
                .post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class).block();
        if(s.startsWith("Successfully"))
            return "Successfully Registered, now you need to login";
        else
            return "Error, try again, possible username already exists";
    }
}
