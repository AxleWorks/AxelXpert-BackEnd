package com.login.AxleXpert.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsByBranch_IdAndStartAt(Long branchId, java.time.LocalDateTime startAt);
}