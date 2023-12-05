package worker.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.ClusterComm.BroadcastSender;
import worker.Query.Query;
import worker.Service.DocumentService;
import worker.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Component
public class NodeLock{
    @Autowired
    private BroadcastSender broadcastSender;
    @Autowired
    private DocumentService documentService;
    private final Map<String, Lock> lockMap = new HashMap<>();
    public Status executeWithLock(Query query) {
        Lock lock = getLockForProperty(query.getFieldName());
        lock.lock();
        try {
            query.setBroadcastMessage(true);
            broadcastSender.sendBroadcast(query);
            return documentService.updateField(query.getDatabaseName(), query.getCollectionName(), query.getIndex(), query.getFieldName(), query.getFieldValue());
        } catch (Exception e) {
            return new Status(Status.StatusType.Failure, "Document not updated");
        } finally {
            lock.unlock();
        }
    }
    private Lock getLockForProperty(String property) {
        synchronized (lockMap) {
            return lockMap.computeIfAbsent(property, k -> new ReentrantLock());
        }
    }

}
