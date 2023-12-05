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
public class AddField implements DocumentHandler {
    @Value("${number-of-nodes}")
    private int numberOfNodes;
    @Value("${node-number}")
    private int nodeNumber;
    @Autowired
    private BroadcastSender broadcastSender;
    @Autowired
    private RequestWrite requestWrite;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private AffinityCalculator affinityCalculator;
    @Autowired
    private DocumentService documentService;
//    private AddField() {
//    }
//    public static AddField getInstance() {
//        return new AddField();
//    }

    @Override
    public Object handleQuery(Query query) {
        JsonNode document = collectionService.findDocumentAsJson(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        int nodeAffinity = affinityCalculator.calculateAffinity(document, numberOfNodes);
        log.info("User: " + query.getUsername() + " tried to add a field in document: " + query.getDatabaseName() + "/" + query.getCollectionName() + "/" + query.getIndex());
        if (nodeAffinity == nodeNumber) {
            Status status =  documentService.addField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName(), query.getFieldValue());
            if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
                query.setBroadcastMessage(true);
                broadcastSender.sendBroadcast(query);
            }
            return status;
        } else if (nodeAffinity != nodeNumber && !query.getBroadcastMessage()) {
            return requestWrite.requestWrite(query, nodeAffinity);
        } else {
            return documentService.addField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName(), query.getFieldValue());
        }
    }
}
