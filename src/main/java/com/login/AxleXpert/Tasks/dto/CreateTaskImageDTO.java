package com.login.AxleXpert.Tasks.dto;

public record CreateTaskImageDTO(
    String imageUrl,
    String description,
    String publicId
) {}