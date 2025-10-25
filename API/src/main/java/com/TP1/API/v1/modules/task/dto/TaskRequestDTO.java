package com.TP1.API.v1.modules.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequestDTO {
     private String title;
     private String description;
     private boolean completed;
}
