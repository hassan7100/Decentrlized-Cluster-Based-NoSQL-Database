package worker.ClusterComm;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import worker.Lock.OptimisticLock;
import worker.Query.Query;
import worker.Query.QueryHandler;
import worker.Query.QueryHandlerFactory;
import worker.Query.QueryType;
import worker.Status;
@Slf4j
@RestController
@RequestMapping("/write")
public class HandleWriteRequest {
    @Autowired
     private QueryHandlerFactory queryHandlerFactory;
    @Autowired
    OptimisticLock optimisticLock;
     @PostMapping("/Query")
     public Status receiveWriteRequest(@RequestBody Query message) {
         log.info("Received write request");
         if (message.getQueryType().equals(QueryType.UpdateField)) {
                 return optimisticLock.executeWithLock(message);
         } else {
                 return (Status) queryHandlerFactory.getQueryHandler(message).handleQuery(message);
         }
     }
}
