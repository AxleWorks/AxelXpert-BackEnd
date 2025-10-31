package com.login.AxleXpert.bookings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.bookings.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBranch_IdAndStartAt(Long branchId, java.time.LocalDateTime startAt);
    boolean existsByCustomerId(Long customerId);
    boolean existsByAssignedEmployeeId(Long employeeId);
}
