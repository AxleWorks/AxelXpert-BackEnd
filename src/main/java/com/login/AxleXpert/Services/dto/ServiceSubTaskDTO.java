package com.login.AxleXpert.Services.dto;

import java.time.LocalDateTime;

public record ServiceSubTaskDTO(
    Long id,
    Long serviceId,
    String title,
    String description,
    Integer orderIndex,
    Boolean isMandatory,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
