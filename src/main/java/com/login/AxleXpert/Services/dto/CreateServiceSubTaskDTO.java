package com.login.AxleXpert.Services.dto;

public record CreateServiceSubTaskDTO(
    String title,
    String description,
    Integer orderIndex,
    Boolean isMandatory
) {}
