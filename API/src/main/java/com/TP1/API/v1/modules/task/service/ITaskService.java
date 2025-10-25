package com.TP1.API.v1.modules.task.service;


import com.TP1.API.v1.modules.task.dto.PageDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTOUpdate;
import com.TP1.API.v1.modules.task.dto.TaskResponseDTO;
import com.TP1.API.v1.modules.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService {
    PageDTO<TaskResponseDTO> findAll(Pageable pageable);
    TaskResponseDTO findById(Long id);
    TaskResponseDTO create(TaskRequestDTO userDTORequest)  ;
    void delete (Long id);
    TaskResponseDTO update(Long id, TaskRequestDTOUpdate taskRequestDTOUpdate);
    TaskResponseDTO completeTask(Long id, boolean completed);
    PageDTO<TaskResponseDTO> findAllByTitleOrDescription(Pageable pageable, String content);
}

