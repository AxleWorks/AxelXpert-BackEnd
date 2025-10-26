package com.login.AxleXpert.Tasks.dto;


import java.time.LocalDateTime;
import java.util.List;

import com.login.AxleXpert.common.enums.TaskStatus;

public record EmployeeTaskDTO(
    Long id,
    String customerName,
    String vehicle,
    Integer durationMinutes,
    String title,
    String description,
    TaskStatus status,
    List<SubTaskDTO> subTasks,
    LocalDateTime startTime,
    LocalDateTime completedTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 
