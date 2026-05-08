package boo.hassie.java.hmcts.dts.tasks.repository;

import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepository extends CrudRepository<Task, Integer> {
    Task findTaskByUuid(String uuid);
}
