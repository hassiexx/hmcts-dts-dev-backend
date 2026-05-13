package boo.hassie.java.hmcts.dts.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ErrorDetail {
    private String field;
    private String reason;
}
