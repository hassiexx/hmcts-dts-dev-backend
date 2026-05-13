package boo.hassie.java.hmcts.dts.tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CreateTaskRequest {
    @NotBlank
    @Length(max = 200)
    @Schema(description = "The title of the task")
    private String title;
    @Length(max = 1000)
    @Schema(description = "The description of the task")
    private String description;
    @NotNull
    @Future
    @Schema(description = "The date and time of when the task is due as a ISO 8601 timestamp")
    private LocalDateTime dueAt;
}
