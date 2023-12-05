package client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;

@ShellComponent("Shutdown Cluster")
public class ShutdownCluster {
    @Autowired
    private WebClient.Builder authBuilder;

    @ShellMethod("Shutdown Cluster")
    public String shutdownCluster() {
        WebClient webClient = authBuilder.baseUrl("http://localhost:8000")
                .build();
        webClient
                .get()
                .uri("/shutdown")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return "Cluster is shutting down";
    }
}
