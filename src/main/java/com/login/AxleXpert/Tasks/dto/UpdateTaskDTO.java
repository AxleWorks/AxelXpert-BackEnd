package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;

import com.login.AxleXpert.common.enums.TaskStatus;

public record UpdateTaskDTO(
    TaskStatus status,
    LocalDateTime startTime,
    LocalDateTime completedTime
   
) {}
