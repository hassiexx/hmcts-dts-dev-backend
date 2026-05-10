package boo.hassie.java.hmcts.dts.tasks.service;

import boo.hassie.java.hmcts.dts.tasks.dto.CreateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import boo.hassie.java.hmcts.dts.tasks.repository.TasksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class TasksServiceTests {
    @InjectMocks
    private TasksService tasksService;

    @Mock
    private TasksRepository tasksRepository;

    @Test
    public void testCreateTask() {
        // Arrange
        final var now = LocalDateTime.now();
        final var request = CreateTaskRequest.builder()
                .title("This is the task title")
                .description("This is the task description")
                .dueAt(now.plusDays(7))
                .build();
        final var task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueAt(request.getDueAt());
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Mockito.when(tasksRepository.save(Mockito.any(Task.class))).thenReturn(task);

        // Act
        var taskDTO = tasksService.createTask(request);

        // Assert
        Assertions.assertEquals(task.getUuid(), taskDTO.getUuid());
        Assertions.assertEquals(task.getTitle(), taskDTO.getTitle());
        Assertions.assertEquals(task.getDescription(), taskDTO.getDescription());
        Assertions.assertEquals(task.getDueAt(), taskDTO.getDueAt());
        Assertions.assertEquals(task.getStatus(), taskDTO.getStatus());
        Assertions.assertEquals(task.getUpdatedAt(), taskDTO.getUpdatedAt());
    }
}
