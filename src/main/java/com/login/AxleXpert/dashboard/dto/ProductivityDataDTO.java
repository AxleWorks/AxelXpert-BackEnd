package com.login.AxleXpert.dashboard.dto;

public record ProductivityDataDTO(
    String hour,
    Integer services,
    Integer efficiency
) {}