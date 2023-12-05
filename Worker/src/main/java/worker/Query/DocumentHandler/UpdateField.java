package worker.Query.DocumentHandler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import worker.AffinityCalculator;
import worker.ClusterComm.RequestWrite;
import worker.Lock.NodeLock;
import worker.Query.Query;
import worker.Service.CollectionService;
import worker.Service.DocumentService;

@Slf4j
@Component
public class UpdateField implements DocumentHandler {
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
    private NodeLock nodeLock;
    @Autowired
    private RequestWrite requestWrite;
    public Object handleQuery(Query query) {
        JsonNode document = collectionService.findDocumentAsJson(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        int nodeAffinity = affinityCalculator.calculateAffinity(document, numberOfNodes);
        log.info("User: "+query.getUsername()+" tried to update a field in document: "+query.getDatabaseName()+"/"+query.getCollectionName()+"/"+query.getIndex());
        if (nodeAffinity == nodeNumber && !query.getBroadcastMessage()) {
            return nodeLock.executeWithLock(query);
        }
        else if (nodeAffinity != nodeNumber && !query.getBroadcastMessage()) {
            JsonNode json = document.get(query.getFieldName());
            query.setOldValue(json);
            return requestWrite.requestWrite(query, nodeAffinity);
        } else {
            return documentService.updateField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName(), query.getFieldValue());
        }
    }

}
