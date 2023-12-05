package worker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
     public enum StatusType {
        Success,
        Failure
    }
    private StatusType statusType;
    private String message;
}
