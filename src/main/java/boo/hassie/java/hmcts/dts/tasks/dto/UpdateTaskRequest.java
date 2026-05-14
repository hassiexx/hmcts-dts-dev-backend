package boo.hassie.java.hmcts.dts.tasks.dto;

import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import boo.hassie.java.hmcts.dts.tasks.validator.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import tools.jackson.databind.annotation.JsonDeserialize;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UpdateTaskRequest {
    @NullOrNotBlank
    @Length(max = 200)
    @Schema(description = "The new title of the task")
    private String title;
    @Length(max = 1000)
    @Schema(description = "The new description of the task")
    private String description;
    @Schema(description = "The new status of the task")
    private Status status;

    @JsonIgnore
    public boolean isEmpty() {
        return title == null && description == null && status == null;
    }
}
