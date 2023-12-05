package com.example.test1.Controller;


import com.example.test1.component.CommandExecutor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownSignal implements ApplicationContextAware {
    @Autowired
    private CommandExecutor commandExecutor;
    private ApplicationContext applicationContext;
    @Value("${num.of.nodes}")
    private int nodeNumber;
    @GetMapping("/shutdown")
    public void shutDown() {
        for (int i = 1; i <= nodeNumber; i++) {
            try {
                commandExecutor.exec("docker", "stop", "node-" + i);
                commandExecutor.exec("docker", "rm", "node-" + i);
            }
            catch (Exception e){
                System.out.println("Error while shutting down node-"+i);
            }
        }
        SpringApplication.exit(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
