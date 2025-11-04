package com.login.AxleXpert.dashboard.dto;

public record BreakdownItemDTO(
    String name,
    Integer value,
    String color,
    Integer count
) {}