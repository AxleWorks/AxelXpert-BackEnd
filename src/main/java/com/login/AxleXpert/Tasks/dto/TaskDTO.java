package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.login.AxleXpert.common.enums.TaskStatus;

public record TaskDTO(
    Long id,
    Long bookingId,
    Long assignedEmployeeId,
    String assignedEmployeeName,
    String title,
    String description,
    TaskStatus status,
    TaskStatus calculatedStatus,
    List<SubTaskDTO> subTasks,
    List<TaskNoteDTO> taskNotes,
    List<TaskImageDTO> taskImages,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}