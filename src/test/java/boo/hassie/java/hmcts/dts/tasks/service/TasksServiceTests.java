package boo.hassie.java.hmcts.dts.tasks.service;

import boo.hassie.java.hmcts.dts.tasks.dto.CreateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.dto.TaskDTO;
import boo.hassie.java.hmcts.dts.tasks.dto.UpdateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import boo.hassie.java.hmcts.dts.tasks.exception.BadRequestException;
import boo.hassie.java.hmcts.dts.tasks.exception.NotFoundException;
import boo.hassie.java.hmcts.dts.tasks.repository.TasksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        final var task = new Task();
        Mockito.when(tasksRepository.findByUuid(task.getUuid())).thenReturn(task);

        Assertions.assertDoesNotThrow(() -> tasksService.deleteTask(task.getUuid()));
        Mockito.verify(tasksRepository, Mockito.times(1)).delete(task);
    }

    @Test
    public void testDeleteTask_TaskDoesNotExist() {
        final var uuid = UUID.randomUUID();
        Mockito.when(tasksRepository.findByUuid(uuid)).thenReturn(null);

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

        Mockito.when(tasksRepository.findByUuid(task.getUuid())).thenReturn(task);

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

        Mockito.when(tasksRepository.findByUuid(uuid)).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> tasksService.getTask(uuid));
    }

    @Test
    public void testGetTasks() {
        // Arrange
        final var tasks = getTestTasks();
        final var orderedTasks = tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .toList();

        Mockito.when(tasksRepository.findAllByOrderByCreatedAtDesc()).thenReturn(orderedTasks);

        // Act
        List<TaskDTO> foundTasks = tasksService.getTasks();

        // Assert
        Assertions.assertEquals(tasks.size(), foundTasks.size());
        Assertions.assertEquals("Task 2", foundTasks.getFirst().getTitle());
        for (var i = 0; i < orderedTasks.size(); i++) {
            final var task = orderedTasks.get(i);
            final var foundTask = foundTasks.get(i);

            Assertions.assertEquals(task.getUuid(), foundTask.getUuid());
            Assertions.assertEquals(task.getTitle(), foundTask.getTitle());
            Assertions.assertEquals(task.getDescription(), foundTask.getDescription());
            Assertions.assertEquals(task.getDueAt(), foundTask.getDueAt());
            Assertions.assertEquals(task.getStatus(), foundTask.getStatus());
            Assertions.assertEquals(task.getUpdatedAt(), foundTask.getUpdatedAt());
        }
    }

    @Test
    public void testGetTasks_NoTasksExist() {
        Mockito.when(tasksRepository.findAllByOrderByCreatedAtDesc()).thenReturn(new ArrayList<>());

        final var foundTasks = tasksService.getTasks();

        Assertions.assertTrue(foundTasks.isEmpty());
    }

    @Test
    public void testUpdateTask() {
        // Arrange
        final Task task = new Task();
        task.setTitle("Task title");
        task.setDescription("Example task description");

        Mockito.when(tasksRepository.findByUuid(task.getUuid())).thenReturn(task);

        // Act
        final UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("New title")
                .description("New description")
                .status(Status.COMPLETED)
                .build();

        tasksService.updateTask(task.getUuid(), request);

        // Assert
        final ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(tasksRepository, Mockito.times(1)).save(captor.capture());

        Assertions.assertEquals(task.getUuid(), captor.getValue().getUuid());
        Assertions.assertEquals(request.getTitle(), captor.getValue().getTitle());
        Assertions.assertEquals(request.getDescription(), captor.getValue().getDescription());
        Assertions.assertEquals(request.getStatus(), captor.getValue().getStatus());
    }

    @Test
    public void testUpdateTask_OneFieldInRequest() {
        // Arrange
        final var task = new Task();
        task.setTitle("Task title");
        task.setDescription("Example task description");

        Mockito.when(tasksRepository.findByUuid(task.getUuid())).thenReturn(task);

        // Act
        final var request = UpdateTaskRequest.builder()
                .status(Status.COMPLETED)
                .build();

        tasksService.updateTask(task.getUuid(), request);

        // Assert
        final var captor = ArgumentCaptor.forClass(Task.class);
        Mockito.verify(tasksRepository, Mockito.times(1)).save(captor.capture());

        Assertions.assertEquals(task.getUuid(), captor.getValue().getUuid());
        Assertions.assertEquals(task.getTitle(), captor.getValue().getTitle());
        Assertions.assertEquals(task.getDescription(), captor.getValue().getDescription());
        Assertions.assertEquals(request.getStatus(), captor.getValue().getStatus());
    }

    @Test
    public void testUpdateTask_TaskDoesNotExist() {
        final var uuid = UUID.randomUUID();
        final var request = UpdateTaskRequest.builder()
                .title("New title")
                .build();

        Mockito.when(tasksRepository.findByUuid(uuid)).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> tasksService.updateTask(uuid, request));
    }

    @Test
    public void testUpdateTask_AllFieldsInRequestNull() {
        final var uuid = UUID.randomUUID();
        final var request = new UpdateTaskRequest();

        Assertions.assertThrows(BadRequestException.class, () -> tasksService.updateTask(uuid, request));
    }

    private List<Task> getTestTasks() {
        final var task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Task 1 desc");
        task1.setStatus(Status.COMPLETED);
        task1.setDueAt(LocalDateTime.now().plusDays(2));
        task1.setCreatedAt(LocalDateTime.now().minusDays(4));
        task1.setUpdatedAt(LocalDateTime.now().minusHours(2));

        final var task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Task 2 desc");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setDueAt(LocalDateTime.now().plusDays(1));
        task2.setCreatedAt(LocalDateTime.now().minusDays(2));
        task2.setUpdatedAt(LocalDateTime.now().minusDays(1));

        return List.of(task1, task2);
    }
}
