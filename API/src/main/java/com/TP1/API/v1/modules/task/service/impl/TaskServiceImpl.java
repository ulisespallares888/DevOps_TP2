package com.TP1.API.v1.modules.task.service.impl;

import com.TP1.API.v1.exceptions.exceptions.ResourceNotFoundException;
import com.TP1.API.v1.modules.task.dto.PageDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTOUpdate;
import com.TP1.API.v1.modules.task.dto.TaskResponseDTO;
import com.TP1.API.v1.modules.task.mapper.MapperTask;
import com.TP1.API.v1.modules.task.model.Task;
import com.TP1.API.v1.modules.task.repository.TaskRepository;
import com.TP1.API.v1.modules.task.service.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;


    @Cacheable(value = "tasks", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public PageDTO<TaskResponseDTO> findAll(Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAll(pageable);

        List<TaskResponseDTO> dtoContent = taskPage.getContent().stream()
                .map(MapperTask.INSTANCIA::taskToTaskResponseDTO)
                .toList();

        return new PageDTO<>(
                dtoContent,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements()
        );
    }



    @Cacheable(value = "tasks", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #content")
    public PageDTO<TaskResponseDTO> findAllByTitleOrDescription(Pageable pageable, String content) {
        Page<Task> taskPage = taskRepository.findAllByTitleContainingOrDescriptionContaining(pageable, content);

        List<TaskResponseDTO> dtoContent = taskPage.getContent().stream()
                .map(MapperTask.INSTANCIA::taskToTaskResponseDTO)
                .toList();

        return new PageDTO<>(
                dtoContent,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements()
        );
    }


    @Cacheable(value = "task", key = "#id")
    public TaskResponseDTO findById(Long id) {
        TaskResponseDTO taskResponseDTO = taskRepository.findById(id)
                .map(MapperTask.INSTANCIA::taskToTaskResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Task no found with ID = %s".formatted(id)));
        return taskResponseDTO;
    }


    @CachePut(value = "task", key = "#result.id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDTO create(TaskRequestDTO userDTORequest) {
        Task task = MapperTask.INSTANCIA.taskRequestDTOToTask(userDTORequest);
        taskRepository.save(task);
        return MapperTask.INSTANCIA.taskToTaskResponseDTO(task);
    }


    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task no found with ID = %s".formatted(id)));
        taskRepository.delete(task);
    }


    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDTO update(Long id, TaskRequestDTOUpdate taskRequestDTOUpdate) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task no found with ID = %s".formatted(id)));

        existingTask = MapperTask.INSTANCIA.taskRequestDTOUpdateToTask(taskRequestDTOUpdate);
        existingTask.setId(id);

        return MapperTask.INSTANCIA.taskToTaskResponseDTO(taskRepository.save(existingTask));
    }

    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDTO completeTask(Long id, boolean completed) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task no found with ID = %s".formatted(id)));

        task.setCompleted(!task.isCompleted());

        //task.setCompleted(completed);
        taskRepository.save(task);
        return MapperTask.INSTANCIA.taskToTaskResponseDTO(task);
    }

}
