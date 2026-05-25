package boo.hassie.java.hmcts.dts.tasks.service;

import boo.hassie.java.hmcts.dts.tasks.dto.CreateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.dto.TaskDTO;
import boo.hassie.java.hmcts.dts.tasks.dto.UpdateTaskRequest;
import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import boo.hassie.java.hmcts.dts.tasks.exception.BadRequestException;
import boo.hassie.java.hmcts.dts.tasks.exception.NotFoundException;
import boo.hassie.java.hmcts.dts.tasks.mapper.TaskMapper;
import boo.hassie.java.hmcts.dts.tasks.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TasksService {
    private final TasksRepository tasksRepository;

    @Autowired
    public TasksService(final TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    /**
     * Creates a new task.
     *
     * @param request the request object.
     * @return the created task.
     */
    public TaskDTO createTask(final CreateTaskRequest request) {
        var task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueAt(request.getDueAt());
        task = tasksRepository.save(task);

        return TaskMapper.INSTANCE.toDTO(task);
    }

    /**
     * Deletes a task.
     *
     * @param uuid the UUID of the task.
     * @throws NotFoundException if the task was not found.
     */
    public void deleteTask(final UUID uuid) {
        final Task task = tasksRepository.findByUuid(uuid);
        if (task == null) {
            throw new NotFoundException("task not found");
        }

        tasksRepository.delete(task);
    }

    /**
     * Gets a task.
     *
     * @param uuid the UUID of the task.
     * @return the task if found.
     * @throws NotFoundException if the task was not found.
     */
    public TaskDTO getTask(final UUID uuid) {
        final Task task = tasksRepository.findByUuid(uuid);

        if (task == null) {
            throw new NotFoundException("task not found");
        }

        return TaskMapper.INSTANCE.toDTO(task);
    }

    /**
     * Gets all tasks.
     *
     * @return a list of tasks.
     */
    public List<TaskDTO> getTasks() {
        final var tasks = tasksRepository.findAllByOrderByCreatedAtDesc();

        return Streamable.of(tasks)
                .map(TaskMapper.INSTANCE::toDTO)
                .toList();
    }

    /**
     * Updates a task.
     *
     * @param uuid    the UUID of the task.
     * @param request the request object.
     * @throws BadRequestException if no fields are provided in the request.
     * @throws NotFoundException   if the task is not found.
     */
    public void updateTask(final UUID uuid, final UpdateTaskRequest request) {
        if (request.isEmpty()) {
            throw new BadRequestException();
        }

        final var task = tasksRepository.findByUuid(uuid);

        if (task == null) {
            throw new NotFoundException("task not found");
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        tasksRepository.save(task);
    }
}
