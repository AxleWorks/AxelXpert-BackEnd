package com.login.AxleXpert.dashboard.dto;

public record EmployeeActivityDTO(
    String action,
    String time,
    String type
) {}