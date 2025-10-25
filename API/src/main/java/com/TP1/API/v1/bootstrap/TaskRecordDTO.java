package com.TP1.API.v1.bootstrap;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRecordDTO {
    private String title;
    private String description;
    private boolean completed;
}
