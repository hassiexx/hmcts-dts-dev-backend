package boo.hassie.java.hmcts.dts.tasks.service;

import boo.hassie.java.hmcts.dts.tasks.dto.CreateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.dto.TaskDTO;
import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import boo.hassie.java.hmcts.dts.tasks.exception.NotFoundException;
import boo.hassie.java.hmcts.dts.tasks.repository.TasksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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

    @Test
    public void testDeleteTask() {
        final var uuid = UUID.randomUUID();
        Mockito.when(tasksRepository.existsByUuid(uuid)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> tasksService.deleteTask(uuid));
        Mockito.verify(tasksRepository, Mockito.times(1)).deleteByUuid(uuid);
    }

    @Test
    public void testDeleteTask_TaskDoesNotExist() {
        final var uuid = UUID.randomUUID();
        Mockito.when(tasksRepository.existsByUuid(uuid)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> tasksService.deleteTask(uuid));
    }

    @Test
    public void testGetTask() {
        // Arrange.
        final var now = LocalDateTime.now();
        final var task = new Task();
        task.setTitle("Example title");
        task.setDescription("Example description");
        task.setDueAt(now.plusDays(10));
        task.setStatus(Status.IN_PROGRESS);
        task.setUpdatedAt(now.minusDays(1));

        Mockito.when(tasksRepository.findTaskByUuid(task.getUuid())).thenReturn(task);

        // Act.
        AtomicReference<TaskDTO> foundTaskAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            foundTaskAtomic.set(tasksService.getTask(task.getUuid()));
        });

        // Assert.
        final var foundTask = foundTaskAtomic.get();
        Assertions.assertEquals(task.getUuid(), foundTask.getUuid());
        Assertions.assertEquals(task.getTitle(), foundTask.getTitle());
        Assertions.assertEquals(task.getDescription(), foundTask.getDescription());
        Assertions.assertEquals(task.getDueAt(), foundTask.getDueAt());
        Assertions.assertEquals(task.getStatus(), foundTask.getStatus());
        Assertions.assertEquals(task.getUpdatedAt(), foundTask.getUpdatedAt());
    }

    @Test
    public void testGetTask_TaskDoesNotExist() {
        final var uuid = UUID.randomUUID();

        Mockito.when(tasksRepository.findTaskByUuid(uuid)).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> tasksService.getTask(uuid));
    }
}
