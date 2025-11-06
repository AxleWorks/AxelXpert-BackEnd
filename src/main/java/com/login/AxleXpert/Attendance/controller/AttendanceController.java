package com.login.AxleXpert.Attendance.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.Attendance.dto.AttendanceCalendarDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceCreateDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceUpdateDTO;
import com.login.AxleXpert.Attendance.service.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    
    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@Valid @RequestBody AttendanceCreateDTO createDTO) {
        try {
            AttendanceDTO attendance = attendanceService.createAttendance(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, 
                                                         @RequestBody AttendanceUpdateDTO updateDTO) {
        try {
            AttendanceDTO attendance = attendanceService.updateAttendance(id, updateDTO);
            return ResponseEntity.ok(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Long id) {
        try {
            AttendanceDTO attendance = attendanceService.getAttendanceById(id);
            return ResponseEntity.ok(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/branch/{branchId}/date/{date}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByBranchAndDate(
            @PathVariable Long branchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendanceList = attendanceService.getAttendanceByBranchAndDate(branchId, date);
        return ResponseEntity.ok(attendanceList);
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByBranchAndDateRange(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendanceList = attendanceService.getAttendanceByBranchAndDateRange(branchId, startDate, endDate);
        return ResponseEntity.ok(attendanceList);
    }
    
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<AttendanceDTO> getAttendanceByUserAndDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<AttendanceDTO> attendance = attendanceService.getAttendanceByUserAndDate(userId, date);
        return attendance.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/calendar")
    public ResponseEntity<List<AttendanceCalendarDTO>> getAttendanceCalendarData(
            @RequestParam(required = false) Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceCalendarDTO> calendarData = attendanceService.getAttendanceCalendarData(branchId, startDate, endDate);
        return ResponseEntity.ok(calendarData);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendance(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
