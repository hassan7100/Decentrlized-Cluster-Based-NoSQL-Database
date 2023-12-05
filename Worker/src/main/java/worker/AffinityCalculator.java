package worker;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class AffinityCalculator {
    int maxValue;
    public int calculateAffinity(JsonNode json, int numOfNodes) {
        synchronized (this) {
            int jsonStr;
            if (json.has("_id")) {
                jsonStr = json.get("_id").asInt();
            } else {
                jsonStr = Math.abs(json.hashCode());
            }
            maxValue = numOfNodes;
            return generateNumber(jsonStr);
        }
    }
    private int generateNumber(int inputNumber) {
        return (inputNumber % maxValue) + 1;
    }
}
