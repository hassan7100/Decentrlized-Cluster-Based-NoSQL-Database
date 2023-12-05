package worker.Lock;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.ClusterComm.BroadcastSender;
import worker.Query.Query;
import worker.Query.QueryType;
import worker.Service.CollectionService;
import worker.Service.DocumentService;
import worker.Status;

import java.lang.annotation.Documented;

@Component
public class OptimisticLock {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private BroadcastSender broadcastSender;
    public Status executeWithLock(Query query)  {
        JsonNode json = collectionService.findDocumentAsJson(query.getDatabaseName(), query.getCollectionName(), query.getIndex());
        try {
            if (json.get(query.getFieldName()).toString().equals(query.getOldValue().toString())) {
                Status status = documentService.updateField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName(), query.getFieldValue());
                if (status.getStatusType().equals(Status.StatusType.Success) && !query.getBroadcastMessage()) {
                    query.setBroadcastMessage(true);
                    broadcastSender.sendBroadcast(query);
                }
                return status;
            } else {
                    return new Status(Status.StatusType.Failure, "Document not updated");
            }
        }
        catch (Exception e) {
                return new Status(Status.StatusType.Failure, "Document not updated");
        }
    }
}
