package com.example.test1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;

@Component
public class UsersDistribution {
    private int nodeOnTurn;
    @Value("${num.of.nodes}")
    private int numContainers;
    public UsersDistribution(){
        nodeOnTurn = 1;
    }
    public int getNodeOnTurn() {
        return (nodeOnTurn++%numContainers)+1;
    }

}
