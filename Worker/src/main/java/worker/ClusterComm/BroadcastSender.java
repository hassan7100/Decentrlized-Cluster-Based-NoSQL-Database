package worker.ClusterComm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import worker.Query.Query;
import org.springframework.http.MediaType;
@Service
@Slf4j
public class BroadcastSender {
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${number-of-nodes}")
    private int numberOfNodes;
    @Value("${node-number}")
    private int nodeNumber;
    public void sendBroadcast(Query query) {
            log.info("Sending broadcast message to nodes, number of nodes: " + numberOfNodes + ", node number: " + nodeNumber);
            for (int i = 1; i <= numberOfNodes; i++) {
                if (i == nodeNumber) {
                    continue;
                }
                webClientBuilder.build().post()
                        .uri("http://node-" + i + ":8080/broadcast/Query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(query), Query.class)
                        .exchange()
                        .block();
            }
    }
}
