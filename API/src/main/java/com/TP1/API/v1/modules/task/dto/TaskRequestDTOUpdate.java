package com.TP1.API.v1.modules.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequestDTOUpdate extends TaskRequestDTO{
    private boolean completed;
}
