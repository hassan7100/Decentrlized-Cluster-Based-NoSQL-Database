package worker.Query.DocumentHandler;


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
import worker.Service.DocumentService;
import worker.Status;

@Slf4j
@Component
public class DeleteField implements DocumentHandler {
    @Autowired
    private DocumentService documentService;
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

    public Object handleQuery(Query query) {
        JsonNode document2 = collectionService.findDocumentAsJson(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        int nodeAffinity = affinityCalculator.calculateAffinity(document2, numberOfNodes);
        log.info("User: "+query.getUsername()+" tried to delete a field in document: "+query.getDatabaseName()+"/"+query.getCollectionName()+"/"+query.getIndex());
        if (nodeAffinity == nodeNumber) {
            Status status =  documentService.removeField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName());
            if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
                query.setBroadcastMessage(true);
                broadcastSender.sendBroadcast(query);
            }
            return status;
        } else if (nodeAffinity != nodeNumber && !query.getBroadcastMessage()) {
            return requestWrite.requestWrite(query, nodeAffinity);
        } else {
            return documentService.removeField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName());
        }
    }
}
