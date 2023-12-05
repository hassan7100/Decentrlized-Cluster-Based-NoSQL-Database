package com.example.test1.Controller;

import com.example.test1.UserHandle.User;
import com.example.test1.UserHandle.UserHandle;
import com.example.test1.component.UsersDistribution;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthenticateUser {
    @Autowired
    UserHandle userHandle;
    @Autowired
    UsersDistribution usersDistribution;
    @GetMapping("/returnNodeNumber")
    public ResponseEntity<ObjectNode> nodeNumber(Authentication authentication) {
        return ResponseEntity.ok(new ObjectNode(new ObjectMapper().getNodeFactory()).put("nodeNumber", userHandle.loginUser(authentication.getName())));
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody JsonNode json){
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.treeToValue(json, User.class);
            user.setNode_number(usersDistribution.getNodeOnTurn());
            user.setRole("ROLE_USER");
            if(userHandle.registerUser(user)){
                log.info("new user registered: "+user.getUsername());
                return ResponseEntity.ok("Successfully Registered, now you need to login");
            }
            else {
                log.error("Error while registering user: "+ user.getUsername());
                return ResponseEntity.ok("Error, try again, possible username already exists");
            }
        }catch (Exception e){
            log.error("Error while registering user", e);
            return ResponseEntity.ok("Error, try again");
        }

    }
}
