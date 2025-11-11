package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;

public record TaskImageDTO(
    Long id,
    Long taskId,
    String imageUrl,
    String publicId,
    String description,
    LocalDateTime createdAt
) {}
