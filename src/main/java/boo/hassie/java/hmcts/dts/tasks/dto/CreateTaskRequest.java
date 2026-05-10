package boo.hassie.java.hmcts.dts.tasks.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private LocalDateTime dueAt;
}
