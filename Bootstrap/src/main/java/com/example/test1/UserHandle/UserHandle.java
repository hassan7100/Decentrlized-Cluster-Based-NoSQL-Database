package com.example.test1.UserHandle;

import com.example.test1.component.PortGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class UserHandle {
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private PortGenerator portGenerator;
    public boolean registerUser(User user) {
        try {
            usersRepo.save(user);
            String s = webClientBuilder
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic yourBase64EncodedCredentials")
                    .filter(ExchangeFilterFunctions.basicAuthentication("BOOTSTRAP", "BOOTSTRAP"))
                    .build()
                    .post()
                    .uri("http://localhost:"+portGenerator.getPort("node-"+user.getNode_number())+"/register/user")
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return s.equals("OK");
        }
        catch (Exception e){
            return false;
        }
    }
    public int loginUser(String username) {
        Optional<User> user = usersRepo.findById(username);
        return portGenerator
                .getPort("node-"+user.get().getNode_number());
    }
}
