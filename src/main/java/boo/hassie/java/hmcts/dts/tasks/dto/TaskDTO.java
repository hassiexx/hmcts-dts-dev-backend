package boo.hassie.java.hmcts.dts.tasks.dto;

import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskDTO {
    private UUID uuid;
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private LocalDateTime dueAt;
    private Status status;
    private LocalDateTime updatedAt;
}
