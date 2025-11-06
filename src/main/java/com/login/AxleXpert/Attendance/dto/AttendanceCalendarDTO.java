package com.login.AxleXpert.Attendance.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCalendarDTO {
    private LocalDate date;
    private Long branchId;
    private String branchName;
    private Long totalEmployees;
    private Long presentCount;
    private Long absentCount;
    private Long shortLeaveCount;
    private Long lateArrivalCount;
    private Long earlyDepartureCount;
}
