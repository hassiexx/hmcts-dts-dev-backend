package boo.hassie.java.hmcts.dts.tasks.mapper;

import boo.hassie.java.hmcts.dts.tasks.dto.TaskDTO;
import boo.hassie.java.hmcts.dts.tasks.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO toDTO(final Task entity);
}
