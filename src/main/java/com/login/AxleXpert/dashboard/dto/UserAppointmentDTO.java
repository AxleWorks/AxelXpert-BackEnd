package com.login.AxleXpert.dashboard.dto;

public record UserAppointmentDTO(
    Long id,
    String service,
    String date,
    String time,
    String vehicle,
    String status
) {}