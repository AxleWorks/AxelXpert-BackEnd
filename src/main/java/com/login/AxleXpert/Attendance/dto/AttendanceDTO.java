package com.login.AxleXpert.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.login.AxleXpert.Attendance.entity.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private Long branchId;
    private String branchName;
    private LocalDate date;
    private LocalTime arrivalTime;
    private LocalTime leaveTime;
    private AttendanceStatus status;
    private String notes;
}
