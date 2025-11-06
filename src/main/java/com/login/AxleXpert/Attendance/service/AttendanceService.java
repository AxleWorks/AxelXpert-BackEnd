package com.login.AxleXpert.Attendance.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Attendance.dto.AttendanceCalendarDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceCreateDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceDTO;
import com.login.AxleXpert.Attendance.dto.AttendanceUpdateDTO;
import com.login.AxleXpert.Attendance.entity.Attendance;
import com.login.AxleXpert.Attendance.repository.AttendanceRepository;
import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;

@Service
@Transactional
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    
    public AttendanceService(AttendanceRepository attendanceRepository,
                           UserRepository userRepository,
                           BranchRepository branchRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }
    
    public AttendanceDTO createAttendance(AttendanceCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Branch branch = branchRepository.findById(createDTO.getBranchId())
            .orElseThrow(() -> new RuntimeException("Branch not found"));
        
        // Check if attendance already exists for this user and date
        Optional<Attendance> existingAttendance = attendanceRepository
            .findByUserIdAndDate(createDTO.getUserId(), createDTO.getDate());
        
        if (existingAttendance.isPresent()) {
            throw new RuntimeException("Attendance record already exists for this user and date");
        }
        
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setBranch(branch);
        attendance.setDate(createDTO.getDate());
        attendance.setArrivalTime(createDTO.getArrivalTime());
        attendance.setLeaveTime(createDTO.getLeaveTime());
        attendance.setStatus(createDTO.getStatus());
        attendance.setNotes(createDTO.getNotes());
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return toDTO(savedAttendance);
    }
    
    public AttendanceDTO updateAttendance(Long id, AttendanceUpdateDTO updateDTO) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        
        if (updateDTO.getArrivalTime() != null) {
            attendance.setArrivalTime(updateDTO.getArrivalTime());
        }
        if (updateDTO.getLeaveTime() != null) {
            attendance.setLeaveTime(updateDTO.getLeaveTime());
        }
        if (updateDTO.getStatus() != null) {
            attendance.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getNotes() != null) {
            attendance.setNotes(updateDTO.getNotes());
        }
        
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return toDTO(updatedAttendance);
    }
    
    public List<AttendanceDTO> getAttendanceByBranchAndDate(Long branchId, LocalDate date) {
        List<Attendance> attendanceList = attendanceRepository.findByBranchIdAndDateWithDetails(branchId, date);
        return attendanceList.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<AttendanceDTO> getAttendanceByBranchAndDateRange(Long branchId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceList = attendanceRepository.findByBranchIdAndDateBetween(branchId, startDate, endDate);
        return attendanceList.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<AttendanceCalendarDTO> getAttendanceCalendarData(Long branchId, LocalDate startDate, LocalDate endDate) {
        if (branchId != null) {
            return attendanceRepository.getAttendanceCalendarData(branchId, startDate, endDate);
        } else {
            return attendanceRepository.getAllAttendanceCalendarData(startDate, endDate);
        }
    }
    
    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        return toDTO(attendance);
    }
    
    public Optional<AttendanceDTO> getAttendanceByUserAndDate(Long userId, LocalDate date) {
        Optional<Attendance> attendance = attendanceRepository.findByUserIdAndDate(userId, date);
        return attendance.map(this::toDTO);
    }
    
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("Attendance record not found");
        }
        attendanceRepository.deleteById(id);
    }
    
    private AttendanceDTO toDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setUserId(attendance.getUser().getId());
        dto.setUsername(attendance.getUser().getUsername());
        dto.setUserEmail(attendance.getUser().getEmail());
        dto.setBranchId(attendance.getBranch().getId());
        dto.setBranchName(attendance.getBranch().getName());
        dto.setDate(attendance.getDate());
        dto.setArrivalTime(attendance.getArrivalTime());
        dto.setLeaveTime(attendance.getLeaveTime());
        dto.setStatus(attendance.getStatus());
        dto.setNotes(attendance.getNotes());
        return dto;
    }
}
