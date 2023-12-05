package worker.ClusterComm;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import worker.Query.Query;
import worker.Query.QueryType;
import worker.Status;

@Service
@Slf4j
public class RequestWrite {
    @Autowired
    private WebClient.Builder webClientBuilder;
    public Status requestWrite(Query query, int nodeNumber) {
            Status status;
            int i = 0;
            if (query.getQueryType().equals("UpdateField")) {
                do {
                    status = webClientBuilder.build().post()
                            .uri("http://node-" + nodeNumber + ":8080/write/Query")
                            .bodyValue(query)
                            .exchange()
                            .flatMap(response ->
                                    response.bodyToMono(Status.class)
                            ).block();
                    i++;
                } while (status.getStatusType().equals(Status.StatusType.Failure) && i < 5);
            } else
                status = webClientBuilder.build().post()
                        .uri("http://node-" + nodeNumber + ":8080/write/Query")
                        .bodyValue(query)
                        .exchange()
                        .flatMap(response ->
                                response.bodyToMono(Status.class)
                        ).block();
            if (status.equals(Status.StatusType.Failure)) {
                log.warn("Failed to write to node: " + nodeNumber);
            } else
                log.info("Successfully wrote to node: " + nodeNumber);
            return status;
    }
}
