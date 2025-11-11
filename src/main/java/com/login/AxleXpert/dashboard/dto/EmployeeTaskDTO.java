package com.login.AxleXpert.dashboard.dto;

public record EmployeeTaskDTO(
    Long id,
    String vehicle,
    String service,
    Integer progress,
    String time,
    String customer,
    String priority
) {}