package com.example.test1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@RequestMapping("/bootstrap")
public class BootstrapController {

    @Autowired
    private UsersRepo usersRepo;

    @PostMapping("/getAddressRegistered")
    public ResponseEntity<String> login(String username){
    System.out.println(username);
    Optional<User> user = usersRepo.findById(username);
    if(user.isPresent()){
        return ResponseEntity.ok(user.get().getEmail());
    }
    return ResponseEntity.ok("Not Found");
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(String username, String password, String email){
        System.out.println(username);
        return ResponseEntity.ok("OK");
    }
}
