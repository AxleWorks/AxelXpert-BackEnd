package com.login.AxleXpert.Attendance.dto;

import java.time.LocalTime;

import com.login.AxleXpert.Attendance.entity.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateDTO {
    private LocalTime arrivalTime;
    private LocalTime leaveTime;
    private AttendanceStatus status;
    private String notes;
}
