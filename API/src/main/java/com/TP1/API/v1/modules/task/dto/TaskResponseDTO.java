package com.TP1.API.v1.modules.task.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskResponseDTO {
    private long id;
    private String title;
    private String description;
    private boolean completed;

}