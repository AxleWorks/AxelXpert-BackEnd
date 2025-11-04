package com.login.AxleXpert.dashboard.dto;

public record BranchPerformanceDTO(
    String branch,
    Integer services,
    Integer revenue,
    Integer efficiency,
    Integer employees
) {}