package com.login.AxleXpert.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Booking status enumeration
 * Represents the different states a booking can be in
 */
public enum BookingStatus {
    PENDING,
    APPROVED,
    COMPLETED,
    CANCELLED;

    /**
     * Serialize enum to its name (uppercase) for JSON output.
     */
    @JsonValue
    public String toValue() {
        return name();
    }

    /**
     * Create enum from JSON input in a case-insensitive way.
     * Accepts values like "Pending", "pending" or "PENDING".
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BookingStatus fromString(String value) {
        if (value == null) return null;
        try {
            return BookingStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown BookingStatus: " + value);
        }
    }
}
