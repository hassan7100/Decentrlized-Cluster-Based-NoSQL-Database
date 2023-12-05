package com.example.test1.component;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@Service
public class PortGenerator {
    private static HashMap<String, Integer> usedPorts;
    private final Random random;
    public PortGenerator() {
        usedPorts = new HashMap<>();
        random = new Random();
    }
    public int getPort(String node){
        int port;
        if(usedPorts.containsKey(node)){
            return usedPorts.get(node);
        }
        do {
            port = random.nextInt(65536);
        } while (isPortInUse(port));
        usedPorts.put(node, port);
        return port;
    }
    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
