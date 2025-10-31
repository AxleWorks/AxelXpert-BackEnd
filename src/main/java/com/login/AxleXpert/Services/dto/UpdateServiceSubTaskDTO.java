package com.login.AxleXpert.Services.dto;

public record UpdateServiceSubTaskDTO(
    String title,
    String description,
    Integer orderIndex,
    Boolean isMandatory
) {}
