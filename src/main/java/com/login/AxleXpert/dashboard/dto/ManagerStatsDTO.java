package com.login.AxleXpert.dashboard.dto;

public record ManagerStatsDTO(
    StatsItemDTO revenue,
    StatsItemDTO users,
    StatsItemDTO bookings,
    StatsItemDTO branches,
    StatsItemDTO performance
) {}