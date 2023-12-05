package worker.Controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import worker.User;
import worker.UserRepo;

@RestController
@Slf4j
public class NewAuthRegistration {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/register/user")
    public String get(@RequestBody User user) {
        try {
            log.info("new register for user with name: "+user.getUsername()+" status: OK");
            if(user.getRole() == null) {
                user.setRole("ROLE_USER");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            return "OK";
        }
        catch (Exception e){
            return "Error";
        }
    }
    @GetMapping("/helloWorld")
    public ResponseEntity<String> hello() {
        try {
            return ResponseEntity.ok("Hello");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Error");
        }
    }

}
