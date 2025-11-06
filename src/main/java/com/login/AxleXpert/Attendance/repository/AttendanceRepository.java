package com.login.AxleXpert.Attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.login.AxleXpert.Attendance.dto.AttendanceCalendarDTO;
import com.login.AxleXpert.Attendance.entity.Attendance;
import com.login.AxleXpert.Attendance.entity.AttendanceStatus;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);
    
    List<Attendance> findByBranchIdAndDate(Long branchId, LocalDate date);
    
    List<Attendance> findByBranchIdAndDateBetween(Long branchId, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a JOIN FETCH a.user u JOIN FETCH a.branch b WHERE a.branch.id = :branchId AND a.date = :date")
    List<Attendance> findByBranchIdAndDateWithDetails(@Param("branchId") Long branchId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.branch.id = :branchId AND a.date = :date AND a.status = :status")
    Long countByBranchIdAndDateAndStatus(@Param("branchId") Long branchId, @Param("date") LocalDate date, @Param("status") AttendanceStatus status);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.branch.id = :branchId AND LOWER(u.role) = 'employee' AND u.is_Active = true")
    Long countTotalEmployeesByBranchId(@Param("branchId") Long branchId);
    
    @Query("SELECT new com.login.AxleXpert.Attendance.dto.AttendanceCalendarDTO(" +
           "a.date, " +
           "a.branch.id, " +
           "a.branch.name, " +
           "(SELECT COUNT(u) FROM User u WHERE u.branch.id = a.branch.id AND LOWER(u.role) = 'employee' AND u.is_Active = true), " +
           "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'SHORT_LEAVE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'LATE_ARRIVAL' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'EARLY_DEPARTURE' THEN 1 ELSE 0 END)) " +
           "FROM Attendance a " +
           "WHERE a.branch.id = :branchId AND a.date BETWEEN :startDate AND :endDate " +
           "GROUP BY a.date, a.branch.id, a.branch.name " +
           "ORDER BY a.date")
    List<AttendanceCalendarDTO> getAttendanceCalendarData(@Param("branchId") Long branchId, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT new com.login.AxleXpert.Attendance.dto.AttendanceCalendarDTO(" +
           "a.date, " +
           "a.branch.id, " +
           "a.branch.name, " +
           "(SELECT COUNT(u) FROM User u WHERE u.branch.id = a.branch.id AND LOWER(u.role) = 'employee' AND u.is_Active = true), " +
           "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'SHORT_LEAVE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'LATE_ARRIVAL' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'EARLY_DEPARTURE' THEN 1 ELSE 0 END)) " +
           "FROM Attendance a " +
           "WHERE a.date BETWEEN :startDate AND :endDate " +
           "GROUP BY a.date, a.branch.id, a.branch.name " +
           "ORDER BY a.date")
    List<AttendanceCalendarDTO> getAllAttendanceCalendarData(@Param("startDate") LocalDate startDate, 
                                                             @Param("endDate") LocalDate endDate);
}
