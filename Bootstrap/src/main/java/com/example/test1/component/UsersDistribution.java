package com.example.test1.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UsersDistribution {
    private AtomicInteger nodeOnTurn;
    @Value("${num.of.nodes}")
    private int numContainers;
    public UsersDistribution(){
        nodeOnTurn = new AtomicInteger(1);
    }
    public int getNodeOnTurn() {
        return (nodeOnTurn.getAndIncrement()%numContainers)+1;
    }
}
