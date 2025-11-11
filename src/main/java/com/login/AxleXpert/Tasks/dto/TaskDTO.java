package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.login.AxleXpert.common.enums.TaskStatus;

public record TaskDTO(
    Long id,
    Long bookingId,
    Long serviceId,
    Long assignedEmployeeId,
    String assignedEmployeeName,
    String vehicle,
    String title,
    String description,
    TaskStatus status,
    TaskStatus calculatedStatus,
    Integer estimatedTimeMinutes,
    List<SubTaskDTO> subTasks,
    List<TaskNoteDTO> taskNotes,
    List<TaskImageDTO> taskImages,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime startTime
) {}
