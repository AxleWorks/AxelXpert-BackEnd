package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;

import com.login.AxleXpert.common.enums.TaskStatus;

public record SubTaskDTO(
    Long id,
    Long taskId,
    String title,
    String description,
    TaskStatus status,
    Integer orderIndex,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
