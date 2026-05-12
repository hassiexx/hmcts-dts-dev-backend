package boo.hassie.java.hmcts.dts.tasks.dto;

import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Status status;

    public boolean isEmpty() {
        return title == null && description == null && status == null;
    }
}
