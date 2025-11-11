package com.login.AxleXpert.bookings.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.common.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBranch_IdAndStartAt(Long branchId, java.time.LocalDateTime startAt);
    boolean existsByCustomerId(Long customerId);
    boolean existsByAssignedEmployeeId(Long employeeId);

    List<Booking> findByCustomerId(Long customerId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customer.id = :customerId AND b.status = :status")
    long countByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") BookingStatus status);
}
