package com.login.AxleXpert.Tasks.repository;

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
    
    List<Task> findByAssignedEmployeeId(Long employeeId);
    
    Optional<Task> findByBookingId(Long bookingId);
    
    @Query("SELECT t FROM Task t WHERE t.booking.customer.id = :customerId")
    List<Task> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT t FROM Task t WHERE t.assignedEmployee.id = :employeeId AND t.status = :status")
    List<Task> findByAssignedEmployeeIdAndStatus(@Param("employeeId") Long employeeId, 
                                                 @Param("status") TaskStatus status);
}