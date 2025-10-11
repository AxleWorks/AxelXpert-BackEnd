package com.login.AxleXpert.common.dto;

/**
 * Standard error response for API endpoints
 * This is a common DTO that can be used across all controllers
 */
public record ErrorResponse(
        String message
) {}