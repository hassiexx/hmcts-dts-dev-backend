package boo.hassie.java.hmcts.dts.tasks.dto;

import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The UUID of the task")
    private UUID uuid;
    @Schema(description = "The title of the task")
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "The description of the task if set")
    private String description;
    @Schema(description = "A ISO 8601 datetime of when the task is due")
    private LocalDateTime dueAt;
    @Schema(description = "The current status of the task")
    private Status status;
    @Schema(description = "An ISO 8601 datetime of when the task was last updated")
    private LocalDateTime updatedAt;
}
