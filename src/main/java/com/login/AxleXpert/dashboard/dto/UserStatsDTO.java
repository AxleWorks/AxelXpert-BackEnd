package com.login.AxleXpert.dashboard.dto;

public record UserStatsDTO(
    StatsItemDTO vehicles,
    StatsItemDTO activeTasks,
    StatsItemDTO serviceHistory,
    StatsItemDTO appointments,
    StatsItemDTO satisfaction
) {}