package worker.Query.DatabaseHandler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.ClusterComm.BroadcastSender;
import worker.Query.Query;
import worker.Service.DatabaseService;
import worker.Status;

@Slf4j
@Component
public class CreateCollection implements DatabaseHandler {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private BroadcastSender broadcastSender;
    public Object handleQuery(Query query) {
        Status status = databaseService.createCollection(query.getDatabaseName(), query.getCollectionName(), query.getSchema());
        log.info("User: "+query.getUsername()+" tried to create a collection named:"+query.getDatabaseName()+"/"+query.getCollectionName()+", status: "+status.getStatusType());
        if(!query.getBroadcastMessage() && status.getStatusType().equals(Status.StatusType.Success)) {
            query.setBroadcastMessage(true);
            broadcastSender.sendBroadcast(query);
        }
        return status;
    }
}
