package com.login.AxleXpert.Tasks.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Tasks.entity.Task;
import com.login.AxleXpert.common.enums.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.booking b LEFT JOIN FETCH b.service WHERE t.assignedEmployee.id = :employeeId")
    List<Task> findByAssignedEmployeeIdWithBooking(@Param("employeeId") Long employeeId);

    Optional<Task> findByBookingId(Long bookingId);

    @Query("SELECT t FROM Task t WHERE t.booking.customer.id = :customerId")
    List<Task> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT t FROM Task t WHERE t.assignedEmployee.id = :employeeId AND t.status = :status")
    List<Task> findByAssignedEmployeeIdAndStatus(@Param("employeeId") Long employeeId, 
                                                 @Param("status") TaskStatus status);
    
    // Check if user has any assigned tasks
    boolean existsByAssignedEmployeeId(Long employeeId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedEmployee.id = :employeeId AND t.status IN :statuses")
    long countByAssignedEmployeeIdAndStatusIn(@Param("employeeId") Long employeeId, 
                                             @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedEmployee.id = :employeeId AND t.status = :status")
    long countByAssignedEmployeeIdAndStatus(@Param("employeeId") Long employeeId, 
                                           @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedEmployee.id = :employeeId AND DATE(t.completedTime) = DATE(:today)")
    long countCompletedTodayByEmployee(@Param("employeeId") Long employeeId, 
                                      @Param("today") LocalDateTime today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.booking.customer.id = :customerId AND t.status IN :statuses")
    long countByCustomerIdAndStatusIn(@Param("customerId") Long customerId, 
                                     @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.booking.customer.id = :customerId AND t.status = :status")
    long countByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                   @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.booking.customer.id = :customerId AND DATE(t.completedTime) = DATE(:today)")
    long countCompletedTodayByCustomer(@Param("customerId") Long customerId, 
                                      @Param("today") LocalDateTime today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.booking.customer.id = :customerId AND MONTH(t.completedTime) = MONTH(:now) AND YEAR(t.completedTime) = YEAR(:now)")
    long countCompletedThisMonthByCustomer(@Param("customerId") Long customerId, 
                                          @Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.assignedEmployee.id = :employeeId ORDER BY t.createdAt DESC")
    List<Task> findByAssignedEmployeeIdOrderByCreatedAtDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.booking b " +
           "LEFT JOIN FETCH b.service " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH t.assignedEmployee " +
           "WHERE t.assignedEmployee.branch.id = :branchId")
    List<Task> findByBranchId(@Param("branchId") Long branchId);
}
