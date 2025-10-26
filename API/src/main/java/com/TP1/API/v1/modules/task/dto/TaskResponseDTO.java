package com.TP1.API.v1.modules.task.dto;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskResponseDTO {
    private long id;
    private String title;
    private String description;
    private boolean completed;
    private String createdAt;
    private String completedAt;
}