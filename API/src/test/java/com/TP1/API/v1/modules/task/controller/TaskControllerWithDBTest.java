package com.TP1.API.v1.modules.task.controller;

import com.TP1.API.v1.modules.task.model.Task;
import com.TP1.API.v1.modules.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerWithDBTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;


    @Test
    @Rollback
    void getAllTasksIntegrationTest() throws Exception {
        Task task1 = taskRepository.save(Task.builder()
                .title("Task 1")
                .description("Description 1")
                .completed(false)
                .build());
        Task task2 = taskRepository.save(Task.builder()
                .title("Task 2")
                .description("Description 2")
                .completed(true)
                .build());

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.taskResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.taskResponseDTOList[0].title").value("Task 1"))
                .andExpect(jsonPath("$._embedded.taskResponseDTOList[1].title").value("Task 2"));
    }


    @Test
    @Rollback
    void createTaskIntegrationTest() throws Exception {
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
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false));
    }



    @Test
    @Rollback
    void completeTaskIntegrationTest() throws Exception {
        Task task = taskRepository.save(Task.builder()
                .title("Task")
                .description("Description")
                .completed(false)
                .build());

        mockMvc.perform(put("/api/v1/tasks/" + task.getId() + "/complete")
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @Rollback
    void deleteTaskIntegrationTest() throws Exception {
        Task task = taskRepository.save(Task.builder()
                .title("Task")
                .description("Description")
                .completed(false)
                .build());

        mockMvc.perform(delete("/api/v1/tasks/" + task.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Rollback
    void getTaskByIdIntegrationTest() throws Exception {
        Task task = taskRepository.save(Task.builder()
                .title("Task")
                .description("Description")
                .completed(false)
                .build());

        mockMvc.perform(get("/api/v1/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @Rollback
    void updateTaskIntegrationTest() throws Exception {
        Task task = taskRepository.save(Task.builder()
                .title("Task")
                .description("Description")
                .completed(false)
                .build());

        mockMvc.perform(put("/api/v1/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Task",
                                    "description": "Updated Description",
                                    "completed": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}
