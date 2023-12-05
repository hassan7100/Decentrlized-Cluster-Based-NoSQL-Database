package worker.Query.DatabaseHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.Query.Query;
import worker.Service.DatabaseService;

@Slf4j
@Component
public class GetCollections implements DatabaseHandler {
    @Autowired
    private DatabaseService databaseService;


    public Object handleQuery(Query query) {
        return databaseService.getCollections(query.getDatabaseName());
    }
}
