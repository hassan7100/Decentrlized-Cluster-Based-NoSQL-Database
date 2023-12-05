package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
//class ClientApplicationTests {
//
//    @Autowired
//    private LoginAndRegisterCommands loginAndRegisterCommands;
//    @Autowired
//    private DatabaseCommands databaseCommands;
//    @Autowired
//    private CollectionCommands collectionCommands;
//    @Autowired
//    private DocumentCommands documentCommands;
//    private final InputStream originalIn = System.in;
//    private ByteArrayInputStream testIn;
//    @Test
//    void loginTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
//        assertEquals("Success", mono.get("statusType").asText());
//    }
//    @Test
//    void signUpTest() {
//        String responseEntity = loginAndRegisterCommands.signUp("hassan", "123", "hassan123@gmail.com");
//        assertEquals("Successfully Registered, now you need to login", responseEntity);
//    }
//    @Test
//    void createDatabaseTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        loginAndRegisterCommands.login();
//        ObjectNode objectNode = databaseCommands.createDatabase("test1");
//        StepVerifier.create(Mono.just(objectNode))
//                .expectNextMatches(objectNode1 -> objectNode1.get("statusType").asText().equals("Success"))
//                .verifyComplete();
//    }
//
//    @Test
//    void createCollectionTest() throws JsonProcessingException {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
//        input = "n\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode objectNode = collectionCommands.createCollection("test1", "test");
//        StepVerifier.create(Mono.just(objectNode))
//                .expectNextMatches(objectNode1 -> objectNode1.get("statusType").asText().equals("Success"))
//                .verifyComplete();
//    }
//    @Test
//    void addDocumentTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
//        ObjectNode objectNode = documentCommands.addDocument("test1", "test", "{\"_id\":1,\"namee\":\"hassan\"}");
//        StepVerifier.create(Mono.just(objectNode))
//                .expectNextMatches(objectNode1 -> objectNode1.get("statusType").asText().equals("Success"))
//                .verifyComplete();
//    }
//    @Test
//    void findDocumentTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
////        List<JsonNode> objectNode = documentCommands.getDocumentByField("test1", "test", "namee", "hassan");
////        assertEquals(objectNode.get(0).toString(), "{\"_id\":1,\"namee\":\"hassan\"}");
//    }
//    @Test
//    void deleteCollectionTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
//        ObjectNode objectNode = collectionCommands.deleteCollection("test1", "test");
//        StepVerifier.create(Mono.just(objectNode))
//                .expectNextMatches(objectNode1 -> objectNode1.get("statusType").asText().equals("Success"))
//                .verifyComplete();
//    }
//    @Test
//    void deleteDatabaseTest() {
//        String input = "admin\nadmin\n";
//        testIn = new ByteArrayInputStream(input.getBytes());
//        System.setIn(testIn);
//        ObjectNode mono = loginAndRegisterCommands.login();
//        System.setIn(originalIn);
//        ObjectNode objectNode = databaseCommands.deleteDatabase("test1");
//        StepVerifier.create(Mono.just(objectNode))
//                .expectNextMatches(objectNode1 -> objectNode1.get("statusType").asText().equals("Success"))
//                .verifyComplete();
//    }
//
//}
//@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
//class ClientApplicationTests {
//    //Write a test for the CommandLine class that tests the following: run method
//    @Autowired
//    private User user;
//    @Autowired
//    private DatabaseCommands databaseCommands;
//    @Autowired
//    private CollectionCommands collectionCommands;
//    @Autowired
//    private DocumentCommands documentCommands;
//    private final InputStream originalIn = System.in;
//    private ByteArrayInputStream testIn;
//    @Test
//    void runTest() throws Exception {
//        user.setUsername("admin");
//        user.setPassword("admin");
//        user.setNodeAddress(38568);
//        System.out.println(user.getPassword());
//        databaseCommands.createDatabase("DB1");
//        collectionCommands.createCollection("DB1", "C1");
//        List<ObjectNode> objectNodes = createListOfObjectNodes();
//        int numRequests = 100;
//        String baseUrl = "https://api.example.com";
//
//        // Create a CountDownLatch with a count of 1 to synchronize the start of all threads
//        CountDownLatch startSignal = new CountDownLatch(1);
//
//        // Create and start multiple threads to send requests concurrently
//        for (int i = 0; i < numRequests; i++) {
//            int requestId = i;
//            Thread thread = new Thread(() -> {
//                try {
//                    // Wait until the startSignal is released
//                    startSignal.await();
//                    addDocument100Requests(objectNodes.get(requestId));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//            thread.start();
//        }
//
//        // Release the threads to start at the same time
//        startSignal.countDown();
//    }
//    private ObjectNode addDocument100Requests(JsonNode jsonNode) {
//        WebClient webClient = WebClient.builder()
//                .baseUrl("http://localhost:" + user.getNodeAddress() + "/collection/create/" + "DB1" + "/" + "C1")
//                .filter(ExchangeFilterFunctions.basicAuthentication(user.getUsername(), user.getPassword()))
//                .build();
//        Mono<ObjectNode> responseMono = webClient
//                .post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(jsonNode)
//                .retrieve()
//                .bodyToMono(ObjectNode.class);
//
//        return responseMono.block();
//    }
//    private List<ObjectNode> createListOfObjectNodes(){
//        List<ObjectNode> objectNodes = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            ObjectNode objectNode = new ObjectMapper().createObjectNode();
//            objectNode.put("_id", i);
//            objectNode.put("name", "name"+i);
//            objectNode.put("age", i);
//            objectNodes.add(objectNode);
//        }
//        return objectNodes;
//    }
//}
//
//
