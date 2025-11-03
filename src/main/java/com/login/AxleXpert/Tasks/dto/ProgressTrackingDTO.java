package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.login.AxleXpert.common.enums.TaskStatus;

public record ProgressTrackingDTO(
    Long id,  
    String customerName, 
    String vehicle,
    Integer durationMinutes,
    String title,
    String description,
    TaskStatus status,
    List<TechnicianNoteInfo> technicianNotes,
    List<String> progressPhotos,
    List<SubTaskDTO> subTasks,
    LocalDateTime startTime, //startTime represents when the task is scheduled/started (NOT database createdAt)
    LocalDateTime completedTime,
    LocalDateTime updatedAt
) {}
