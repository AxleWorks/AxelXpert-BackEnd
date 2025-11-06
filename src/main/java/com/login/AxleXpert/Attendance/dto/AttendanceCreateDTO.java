package com.login.AxleXpert.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.login.AxleXpert.Attendance.entity.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreateDTO {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long branchId;
    
    @NotNull
    private LocalDate date;
    
    private LocalTime arrivalTime;
    
    private LocalTime leaveTime;
    
    @NotNull
    private AttendanceStatus status;
    
    private String notes;
}
