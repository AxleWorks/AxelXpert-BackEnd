package com.login.AxleXpert.dashboard.dto;

public record UserRecentTaskDTO(
    Long id,
    String title,
    String status,
    String vehicle,
    String date
) {}