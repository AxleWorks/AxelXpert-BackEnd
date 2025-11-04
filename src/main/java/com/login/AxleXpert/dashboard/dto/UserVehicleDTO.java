package com.login.AxleXpert.dashboard.dto;

public record UserVehicleDTO(
    Long id,
    String make,
    String model,
    Integer year,
    String licensePlate,
    String serviceStatus,
    String lastService
) {}