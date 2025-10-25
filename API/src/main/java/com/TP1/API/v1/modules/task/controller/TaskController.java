package com.TP1.API.v1.modules.task.controller;

import com.TP1.API.v1.exceptions.exceptions.InvalidRequestException;
import com.TP1.API.v1.modules.task.dto.PageDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTO;
import com.TP1.API.v1.modules.task.dto.TaskRequestDTOUpdate;
import com.TP1.API.v1.modules.task.dto.TaskResponseDTO;
import com.TP1.API.v1.modules.task.service.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;
    private final PagedResourcesAssembler<TaskResponseDTO> pagedResourcesAssembler;

/*
    @GetMapping("")
    public ResponseEntity<PageDTO<TaskResponseDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        PageDTO<TaskResponseDTO> pageDTO = taskService.findAll(pageable);

        return ResponseEntity.ok(pageDTO);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<PageDTO<TaskResponseDTO>> searchTasks(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        PageDTO<TaskResponseDTO> pageDTO = taskService.findAllByTitleOrDescription(pageable,content);

        return ResponseEntity.ok(pageDTO);
    }

 */

    private PagedModel<EntityModel<TaskResponseDTO>> toPagedModel(Page<TaskResponseDTO> taskResponseDTOPage) {
        return pagedResourcesAssembler.toModel(
                taskResponseDTOPage,
                taskResponseDTO -> {

                    EntityModel<TaskResponseDTO> taskResponseDTOEntityModelEntityModel = EntityModel.of(taskResponseDTO);

                    taskResponseDTOEntityModelEntityModel.add(
                            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class)
                                    .findById(taskResponseDTO.getId())).withSelfRel());

                    return taskResponseDTOEntityModelEntityModel;
                }
        );
    }


    @GetMapping("")
    public ResponseEntity<PagedModel<EntityModel<TaskResponseDTO>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        PageDTO<TaskResponseDTO> pageDTO = taskService.findAll(pageable);

        Page<TaskResponseDTO> taskResponseDTOPage = new PageImpl<>(
                pageDTO.getContent(),
                PageRequest.of(pageDTO.getPage(), pageDTO.getSize(), sortOrder),
                pageDTO.getTotalElements()
        );

        if (taskResponseDTOPage.isEmpty()) {
            return ResponseEntity.ok(PagedModel.empty());
        }

        return ResponseEntity.ok(toPagedModel(taskResponseDTOPage));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<PagedModel<EntityModel<TaskResponseDTO>>> searchTasks(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        PageDTO<TaskResponseDTO> pageDTO = taskService.findAllByTitleOrDescription(pageable,content);

        Page<TaskResponseDTO> taskResponseDTOPage = new PageImpl<>(
                pageDTO.getContent(),
                PageRequest.of(pageDTO.getPage(), pageDTO.getSize(), sortOrder),
                pageDTO.getTotalElements()
        );

        if (taskResponseDTOPage.isEmpty()) {
            return ResponseEntity.ok(PagedModel.empty());
        }

        return ResponseEntity.ok(toPagedModel(taskResponseDTOPage));
    }



    @GetMapping(value = "{id}")
    public TaskResponseDTO findById(@Valid @PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        validate(taskRequestDTO);
        return taskService.create(taskRequestDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(value = "{id}")
    public TaskResponseDTO update(@Valid @PathVariable Long id, @RequestBody TaskRequestDTOUpdate taskRequestDTOUpdate) {
        validate(taskRequestDTOUpdate);
        return taskService.update(id, taskRequestDTOUpdate);
    }

    @PutMapping(value = "{id}/complete")
    public TaskResponseDTO completeTask(@Valid @PathVariable Long id, @RequestParam boolean completed) {
        return  taskService.completeTask(id, completed);
    }

    private void validate(TaskRequestDTO taskRequestDTO) {
        if (taskRequestDTO.getTitle() == null || taskRequestDTO.getTitle().isEmpty()) {
            throw new InvalidRequestException("Title cannot be null or empty");
        }
        if (taskRequestDTO.getDescription() == null || taskRequestDTO.getDescription().isEmpty()) {
            throw new InvalidRequestException("Description cannot be null or empty");
        }
    }
}
