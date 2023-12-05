package com.example.test1;

import com.example.test1.UserHandle.User;
import com.example.test1.UserHandle.UserHandle;
import com.example.test1.UserHandle.UsersRepo;
import com.example.test1.component.CommandExecutor;
import com.example.test1.component.PortGenerator;
import com.example.test1.component.UsersDistribution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class Test1Application {
    public static void main(String[] args) {
        SpringApplication.run(Test1Application.class, args);
    }
}
@Service
@Slf4j
class CommandLine implements CommandLineRunner {
    @Value("${num.of.nodes}")
    private int numOfNodes;
    @Autowired
    UsersDistribution usersDistribution;
    @Autowired
    UserHandle userHandle;
    @Autowired
    private PortGenerator portGenerator;
    @Autowired
    private CommandExecutor commandExecutor;
    private final UsersRepo usersRepo;
    public CommandLine(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }
    @Override
    public void run(String... args) throws Exception {
        commandExecutor.exec("docker build -t worker ./Docker");
        for (int i = 1; i <= numOfNodes; i++) {
            try {
                commandExecutor.exec("docker",
                        "run",
                        "-d",
                        "-p",
                        portGenerator.getPort("node-" + i) + ":8080",
                        "--name", "node-" + i,
                        "--network", "cluster",
                        "--ip", "10.1.4." + i,
                        "--env", "NUMBER_OF_NODES=" + numOfNodes,
                        "--env", "NODE_NUMBER=" + i,
                        "worker"
                );
            } catch (Exception e) {
                log.error("Error while starting node-" + i);
                log.info("Shutting down all nodes");
                for (int j = 1; j <= numOfNodes; j++) {
                    try {
                        commandExecutor.exec("docker", "stop", "node-" + j);
                        commandExecutor.exec("docker", "rm", "node-" + j);
                    } catch (Exception ex) {
                        log.error("Error while shutting down node-" + j);
                    }
                }
            }
        }
        Thread.sleep(1000 * 50);
        userHandle.registerUser(User.builder()
                .username("admin")
                .password("admin")
                .email("admin@gmail.com")
                .node_number(usersDistribution.getNodeOnTurn())
                .role("ROLE_ADMIN")
                .build());
        userHandle.registerUser(User.builder()
                .username("user")
                .password("user")
                .email("user@gmail.com")
                .node_number(usersDistribution.getNodeOnTurn())
                .role("ROLE_USER")
                .build());
    }

}
