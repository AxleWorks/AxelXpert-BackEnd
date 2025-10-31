package com.login.AxleXpert.Tasks.dto;

import com.login.AxleXpert.common.enums.TaskStatus;

public record UpdateSubTaskDTO(
    String title,
    String description,
    TaskStatus status,
    String notes,
    Integer orderIndex
) {}
