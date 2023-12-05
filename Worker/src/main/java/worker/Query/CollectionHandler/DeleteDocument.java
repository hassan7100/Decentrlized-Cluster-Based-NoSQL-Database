package worker.Query.CollectionHandler;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import worker.AffinityCalculator;
import worker.ClusterComm.BroadcastSender;
import worker.ClusterComm.RequestWrite;
import worker.Query.Query;
import worker.Service.CollectionService;
import worker.Status;

@Slf4j
@Component
public class DeleteDocument implements CollectionHandler {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private AffinityCalculator affinityCalculator;
    @Value("${number-of-nodes}")
    private int numberOfNodes;
    @Value("${node-number}")
    private int nodeNumber;
    @Autowired
    private BroadcastSender broadcastSender;
    @Autowired
    private RequestWrite requestWrite;

    public Status handleQuery(Query query) {
        log.info("User: "+query.getUsername()+" tried to delete a document in collection: "+query.getDatabaseName()+"/"+query.getCollectionName());
        JsonNode document1 = collectionService.findDocumentAsJson(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        int nodeAffinity = affinityCalculator.calculateAffinity(document1, numberOfNodes);
        if (nodeAffinity == nodeNumber) {
            Status status =  collectionService.removeDocument(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
            if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
                query.setBroadcastMessage(true);
                broadcastSender.sendBroadcast(query);
            }
            return status;

        } else if (nodeAffinity != nodeNumber && !query.getBroadcastMessage()) {
            return requestWrite.requestWrite(query, nodeAffinity);
        } else if (query.getBroadcastMessage()) {
            return collectionService.removeDocument(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        }
        return new Status(Status.StatusType.Success, "Document deleted successfully");
    }
}
