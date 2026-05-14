package boo.hassie.java.hmcts.dts.tasks.controller;

import boo.hassie.java.hmcts.dts.tasks.dto.CreateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.dto.TaskDTO;
import boo.hassie.java.hmcts.dts.tasks.dto.UpdateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.entity.Status;
import boo.hassie.java.hmcts.dts.tasks.exception.NotFoundException;
import boo.hassie.java.hmcts.dts.tasks.service.TasksService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TasksControllerTests {
    private static final String BASE_URL = "/tasks/";

    private AutoCloseable closeableMocks;
    private MockMvc mockMvc;
    private JsonMapper mapper;

    @InjectMocks
    private TasksController tasksController;

    @Mock
    private TasksService tasksService;

    @BeforeEach
    public void setup() {
        closeableMocks = MockitoAnnotations.openMocks(TasksControllerTests.class);
        mapper = JsonMapper.builder()
                .findAndAddModules()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(tasksController)
                .setControllerAdvice(new ControllerExceptionHandler(mapper))
                .setMessageConverters(new JacksonJsonHttpMessageConverter(mapper))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @AfterEach
    public void teardown() throws Exception {
        closeableMocks.close();
    }

    @Test
    public void testCreateTask() throws Exception {
        // Arrange
        final var request = CreateTaskRequest.builder()
                .title("Task title")
                .description("Task description")
                .dueAt(LocalDateTime.now().plusDays(10))
                .build();

        final var task = TaskDTO.builder()
                .title("Task title")
                .description("Task description")
                .uuid(UUID.randomUUID())
                .status(Status.TO_DO)
                .dueAt(LocalDateTime.now().plusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(tasksService.createTask(request)).thenReturn(task);

        // Act
        final var result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(task)));
    }

    @Test
    public void testCreateTask_EmptyTitleAndDueDate() throws Exception {
        final var request = new CreateTaskRequest();

        final var result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.length()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[*].field",
                        Matchers.containsInAnyOrder("title", "due_at")));
    }

    @Test
    public void testCreateTask_DueDateInPast() throws Exception {
        final var request = CreateTaskRequest.builder()
                .title("Task title")
                .description("Task description")
                .dueAt(LocalDateTime.now().minusSeconds(1))
                .build();

        final var result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.length()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].field", Matchers.is("due_at")));
    }

    @Test
    public void testDeleteTask() throws Exception {
        final UUID uuid = UUID.randomUUID();

        final var result = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    public void testDeleteTask_TaskDoesNotExist() throws Exception {
        final UUID uuid = UUID.randomUUID();
        Mockito.doThrow(new NotFoundException("task not found")).when(tasksService).deleteTask(uuid);

        final var result = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("task not found")));
    }

    @Test
    public void testDeleteTask_InvalidUUID() throws Exception {
        final var uuid = "test123";

        final var result = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.length()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].param", Matchers.is("uuid")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].reason",
                        Matchers.is("Invalid UUID string: test123")));
    }

    @Test
    public void testGetTask() throws Exception {
        final var task = TaskDTO.builder()
                .uuid(UUID.randomUUID())
                .title("Task title")
                .description("Task description")
                .status(Status.COMPLETED)
                .dueAt(LocalDateTime.now().plusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Mockito.when(tasksService.getTask(task.getUuid())).thenReturn(task);

        final var result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + task.getUuid()));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertEquals(task,
                mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), TaskDTO.class));
    }

    @Test
    public void testGetTask_TaskDoesNotExist() throws Exception {
        final UUID uuid = UUID.randomUUID();
        Mockito.doThrow(new NotFoundException("task not found")).when(tasksService).getTask(uuid);

        final var result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("task not found")));
    }

    @Test
    public void testGetTask_InvalidUUID() throws Exception {
        final var uuid = "test123";

        final var result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.length()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].param", Matchers.is("uuid")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].reason",
                        Matchers.is("Invalid UUID string: test123")));
    }

    @Test
    public void testGetTasks() throws Exception {
        final var tasks = List.of(
                TaskDTO.builder()
                        .uuid(UUID.randomUUID())
                        .title("Task 1")
                        .description("Task 1 description")
                        .status(Status.IN_PROGRESS)
                        .dueAt(LocalDateTime.now().plusDays(7))
                        .updatedAt(LocalDateTime.now().minusDays(4))
                        .build(),
                TaskDTO.builder()
                        .uuid(UUID.randomUUID())
                        .title("Task 2")
                        .description("Task 2 description")
                        .status(Status.TO_DO)
                        .dueAt(LocalDateTime.now().plusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(2))
                        .build()
        );

        Mockito.when(tasksService.getTasks()).thenReturn(tasks);

        final var result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].uuid",
                        Matchers.containsInAnyOrder(tasks.get(0).getUuid().toString(), tasks.get(1).getUuid().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].title",
                        Matchers.containsInAnyOrder("Task 1", "Task 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].status",
                        Matchers.containsInAnyOrder("TO_DO", "IN_PROGRESS")));
    }

    @Test
    public void testGetTasks_ZeroTasks() throws Exception {
        Mockito.when(tasksService.getTasks()).thenReturn(new ArrayList<>());

        final var result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(0)));
    }

    @Test
    public void testUpdateTask() throws Exception {
        final var uuid = UUID.randomUUID();
        final var request = UpdateTaskRequest.builder()
                .title("New title")
                .description("New desc")
                .status(Status.COMPLETED)
                .build();

        final var result = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateTask_EmptyTitle() throws Exception {
        final var uuid = UUID.randomUUID();
        final var request = UpdateTaskRequest.builder()
                .title("")
                .build();

        final var result = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].field", Matchers.is("title")));
    }

    @Test
    public void testUpdateTask_TaskDoesNotExist() throws Exception {
        final var uuid = UUID.randomUUID();
        final var request = UpdateTaskRequest.builder()
                .status(Status.COMPLETED)
                .build();

        Mockito.doThrow(new NotFoundException("task not found")).when(tasksService).updateTask(uuid, request);

        final var result = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("task not found")));
    }

    @Test
    public void testUpdateTask_InvalidUUID() throws Exception {
        final var uuid = "test123";

        final var result = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + uuid));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Bad request")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_details[0].param", Matchers.is("uuid")));
    }
}
