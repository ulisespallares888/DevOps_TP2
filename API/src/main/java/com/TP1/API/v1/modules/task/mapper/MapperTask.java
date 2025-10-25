package com.TP1.API.v1.modules.task.mapper;

import com.TP1.API.v1.modules.task.dto.TaskRequestDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTOUpdate;
import com.TP1.API.v1.modules.task.dto.TaskResponseDTO;
import com.TP1.API.v1.modules.task.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapperTask {
    MapperTask INSTANCIA = Mappers.getMapper(MapperTask.class);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "completed", constant = "false")
    Task taskRequestDTOToTask(TaskRequestDTO taskRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "completed", source = "completed")
    TaskResponseDTO taskToTaskResponseDTO(Task task);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "completed", source = "completed")
    Task taskRequestDTOUpdateToTask(TaskRequestDTOUpdate TaskRequestDTOUpdate);

}
