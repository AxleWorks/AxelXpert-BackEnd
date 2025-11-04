package com.login.AxleXpert.dashboard.dto;

import java.util.List;

public record StatsItemDTO(
    String value,
    String trend,
    List<DetailItemDTO> details
) {}