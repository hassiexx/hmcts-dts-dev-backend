package boo.hassie.java.hmcts.dts.tasks.repository;

import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TasksRepository extends CrudRepository<Task, Integer> {
    /**
     * Deletes a task by UUID.
     * @param uuid the UUID to find.
     */
    void deleteByUuid(UUID uuid);

    /**
     * Returns whether a task exists by UUID.
     * @param uuid the UUID to find.
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Finds a task by UUID.
     * @param uuid the UUID to find.
     * @return the task if found.
     */
    Task findTaskByUuid(UUID uuid);
}
