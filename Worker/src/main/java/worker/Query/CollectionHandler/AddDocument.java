package worker.Query.CollectionHandler;


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
public class AddDocument implements CollectionHandler {
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
        int affinityNode = affinityCalculator.calculateAffinity(query.getDocument(), numberOfNodes);
        log.info("User: "+query.getUsername()+" tried to add a document in collection: "+query.getDatabaseName()+"/"+query.getCollectionName());
        if (affinityNode == nodeNumber) {
            Status status = collectionService.addDocument(query.getDatabaseName(), query.getCollectionName(), query.getDocument());
            if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
                query.setBroadcastMessage(true);
                broadcastSender.sendBroadcast(query);
            }
            return status;
        } else if (affinityNode != nodeNumber && !query.getBroadcastMessage()) {
            return requestWrite.requestWrite(query, affinityNode);
        } else if (query.getBroadcastMessage()) {
            return collectionService.addDocument(query.getDatabaseName(), query.getCollectionName(), query.getDocument());
        }
        return new Status(Status.StatusType.Success, "Document inserted successfully");
    }
}
