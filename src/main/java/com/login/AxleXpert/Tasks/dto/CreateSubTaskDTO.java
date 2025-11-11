package com.login.AxleXpert.Tasks.dto;

public record CreateSubTaskDTO(
    String title,
    String description,
    Integer orderIndex
) {}
