package worker.Query.DatabaseHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.ClusterComm.BroadcastSender;
import worker.Query.Query;
import worker.Service.DatabaseService;
import worker.Status;

@Component
@Slf4j
public class DeleteCollection implements DatabaseHandler {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private BroadcastSender broadcastSender;

    public Object handleQuery(Query query) {
        Status status = databaseService.dropCollection(query.getDatabaseName(), query.getCollectionName());
        log.info("User: "+query.getUsername()+" tried to delete a collection named:"+query.getDatabaseName()+"/"+query.getCollectionName()+", status: "+status.getStatusType());
        if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
            query.setBroadcastMessage(true);
            broadcastSender.sendBroadcast(query);
        }
        return status;
    }
}
