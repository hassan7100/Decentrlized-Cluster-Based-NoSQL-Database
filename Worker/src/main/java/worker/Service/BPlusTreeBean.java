package worker.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import worker.Access.BPlusTree;
import worker.Schema.SchemaValidator;

@Configuration
public class BPlusTreeBean {
    @Bean
    public BPlusTree<String, CollectionIndexer> collections() {
        return new BPlusTree<>();
    }
}