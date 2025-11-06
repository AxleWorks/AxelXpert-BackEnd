package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.login.AxleXpert.common.enums.TaskStatus;

// Used to provide comprehensive task tracking across the branch for management oversight.

public record ManagerProgressTrackingDTO(
    Long id,
    Long bookingId,
    String customerName, 
    String vehicle,
    String assignedEmployeeName,
    Integer durationMinutes,
    String title,
    String description,
    TaskStatus status,
    List<String> progressPhotos,
    List<SubTaskDTO> subTasks,
    LocalDateTime startTime,
    LocalDateTime completedTime,
    LocalDateTime updatedAt
) {}
