package com.login.AxleXpert.dashboard.dto;

public record RevenueDataDTO(
    String month,
    Integer revenue,
    Integer services,
    Integer customers
) {}