package com.login.AxleXpert.BookingCalender.UserBookings;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Flat Booking DTO used by the demo controller and JSON resource.
 */
public record BookingDTO(
        Long id,
        Long customerId,
        String customerName,
        String customerPhone,
        String vehicle,
        Long branchId,
        String branchName,
        Long serviceId,
        String serviceName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        BookingStatus status,
        Long assignedEmployeeId,
        String assignedEmployeeName,
        BigDecimal totalPrice,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
