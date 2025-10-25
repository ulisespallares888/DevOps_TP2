package com.TP1.API.v1.modules.task.controller;

import com.TP1.API.v1.modules.task.dto.PageDTO;
import com.TP1.API.v1.modules.task.dto.TaskResponseDTO;
import com.TP1.API.v1.modules.task.service.ITaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
class TaskControllerWithOutDBTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITaskService taskService;

    @Test
    void getAllTasksTest() throws Exception {
        when(taskService.findAll(any(Pageable.class)))
                .thenReturn(new PageDTO<>(List.of(
                        new TaskResponseDTO(1L, "Task 1", "Description 1", false),
                        new TaskResponseDTO(2L, "Task 2", "Description 2", true)
                ), 0, 10, 2));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void searchTasksTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskService.findAllByTitleOrDescription(any(Pageable.class), any(String.class)))
                .thenReturn(new PageDTO<>(List.of(
                        new TaskResponseDTO(1L, "Task 1", "Description 1", false),
                        new TaskResponseDTO(2L, "Task 2", "Description 2", true)
                ), 0, 10, 2));

        mockMvc.perform(get("/api/v1/tasks/search")
                .param("content", "Task")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }


    @Test
    void createTaskTest() throws Exception {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(1L, "New Task", "New Description", false);

        // Mockeamos el servicio
        when(taskService.create(any())).thenReturn(taskResponseDTO);

        // MockMvc: enviamos POST con JSON
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "New Task",
                    "description": "New Description",
                    "completed": false
                }
            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }


    @Test
    void getTaskByIdTest() throws Exception {
        when(taskService.findById(1L))
                .thenReturn(new TaskResponseDTO(1L, "Task 1", "Description 1", false));

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTaskTest() throws Exception {
        // Mockeamos el método void
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void completeTaskTest() throws Exception {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(1L, "Task 1", "Description 1", true);

        // Mockeamos el servicio
        when(taskService.completeTask(1L, true)).thenReturn(taskResponseDTO);

        // MockMvc: enviamos PUT con JSON
        mockMvc.perform(put("/api/v1/tasks/1/complete")
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.completed").value(true));
    }


    @Test
    void updateTaskTest() throws Exception {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(1L, "Updated Task", "Updated Description", false);

        // Mockeamos el servicio
        when(taskService.update(any(Long.class), any())).thenReturn(taskResponseDTO);

        // MockMvc: enviamos PUT con JSON
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "Updated Task",
                    "description": "Updated Description",
                    "completed": false
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

}

