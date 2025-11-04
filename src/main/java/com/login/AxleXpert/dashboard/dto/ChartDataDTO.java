package com.login.AxleXpert.dashboard.dto;

public record ChartDataDTO(
    String month,
    Integer services,
    Integer cost
) {}