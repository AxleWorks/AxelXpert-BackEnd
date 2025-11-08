package com.login.AxleXpert.Tasks.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.Tasks.dto.CreateSubTaskDTO;
import com.login.AxleXpert.Tasks.dto.CreateTaskImageDTO;
import com.login.AxleXpert.Tasks.dto.CreateTaskNoteDTO;
import com.login.AxleXpert.Tasks.dto.EmployeeTaskDTO;
import com.login.AxleXpert.Tasks.dto.ManagerProgressTrackingDTO;
import com.login.AxleXpert.Tasks.dto.UserProgressTrackingDTO;
import com.login.AxleXpert.Tasks.dto.SubTaskDTO;
import com.login.AxleXpert.Tasks.dto.TaskDTO;
import com.login.AxleXpert.Tasks.dto.TaskImageDTO;
import com.login.AxleXpert.Tasks.dto.TaskNoteDTO;
import com.login.AxleXpert.Tasks.dto.UpdateSubTaskDTO;
import com.login.AxleXpert.Tasks.dto.UpdateTaskDTO;
import com.login.AxleXpert.Tasks.service.TaskService;
import com.login.AxleXpert.common.dto.ErrorResponse;
import com.login.AxleXpert.common.enums.NoteType;

@CrossOrigin(origins = "http://localhost:5173") 
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeTaskDTO>> getTasksByEmployee(@PathVariable Long employeeId) {
        List<EmployeeTaskDTO> tasks = taskService.getTasksByEmployee(employeeId);
        return ResponseEntity.ok(tasks);

    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TaskDTO>> getTasksByCustomer(@PathVariable Long customerId) {
        List<TaskDTO> tasks = taskService.getTasksByCustomer(customerId);
        return ResponseEntity.ok(tasks);
    }

    //New endpoint for Customer Progress Tracking Feature
    @GetMapping("/customer/{customerId}/progress-tracking")
    public ResponseEntity<List<UserProgressTrackingDTO>> getCustomerProgressTracking(@PathVariable Long customerId) {
        List<UserProgressTrackingDTO> tasks = taskService.getTasksForCustomerProgressTracking(customerId);
        return ResponseEntity.ok(tasks);
    }

    //New endpoint for Manager Progress Tracking Feature
    @GetMapping("/manager/{managerId}/progress-tracking")
    public ResponseEntity<?> getManagerProgressTracking(@PathVariable Long managerId) {
        try {
            List<ManagerProgressTrackingDTO> tasks = taskService.getTasksForManagerProgressTracking(managerId);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable Long taskId) {
        Optional<TaskDTO> task = taskService.getTaskById(taskId);
        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Task not found with id: " + taskId));
        }
        return ResponseEntity.ok(task.get());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getTaskByBooking(@PathVariable Long bookingId) {
        Optional<TaskDTO> task = taskService.getTaskByBookingId(bookingId);
        if (task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Task not found for booking id: " + bookingId));
        }
        return ResponseEntity.ok(task.get());
    }


    // Implemented a new endpoint to update task status, starttime and end time, below endpoint is commented for refeence.

    // @PutMapping("/{taskId}/status")
    // public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, 
    //                                         @RequestParam TaskStatus status) {
    //     try {
    //         TaskDTO updatedTask = taskService.updateTaskStatus(taskId, status);
    //         return ResponseEntity.ok(updatedTask);
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                 .body(new ErrorResponse(e.getMessage()));
    //     }
    // }

    @PatchMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, 
                                        @RequestBody UpdateTaskDTO updateTaskDTO) {
        try {
            TaskDTO updatedTask = taskService.updateTask(taskId, updateTaskDTO);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // SubTask endpoints
    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<?> addSubTask(@PathVariable Long taskId, 
                                       @RequestBody CreateSubTaskDTO createSubTaskDTO) {
        try {
            SubTaskDTO subTask = taskService.addSubTask(taskId, createSubTaskDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(subTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> updateSubTask(@PathVariable Long subTaskId, 
                                         @RequestBody UpdateSubTaskDTO updateSubTaskDTO) {
        try {
            SubTaskDTO updatedSubTask = taskService.updateSubTask(subTaskId, updateSubTaskDTO);
            return ResponseEntity.ok(updatedSubTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@PathVariable Long subTaskId) {
        try {
            taskService.deleteSubTask(subTaskId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Task Note endpoints
    @PostMapping("/{taskId}/notes")
    public ResponseEntity<?> addTaskNote(@PathVariable Long taskId, 
                                       @RequestParam Long authorId,
                                       @RequestBody CreateTaskNoteDTO createTaskNoteDTO) {
        try {
            TaskNoteDTO note = taskService.addTaskNote(taskId, authorId, createTaskNoteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(note);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{taskId}/notes")
    public ResponseEntity<List<TaskNoteDTO>> getTaskNotes(@PathVariable Long taskId,
                                                         @RequestParam(required = false) NoteType noteType) {
        List<TaskNoteDTO> notes = taskService.getTaskNotes(taskId, noteType);
        return ResponseEntity.ok(notes);
    }

    @DeleteMapping("{taskId}/notes/{noteId}")
    public ResponseEntity<?> deleteTaskNote(@PathVariable Long noteId) {
        try {
            taskService.deleteTaskNote(noteId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Task Image endpoints
    @PostMapping("/{taskId}/images")
    public ResponseEntity<?> addTaskImage(@PathVariable Long taskId,
                                        @RequestBody CreateTaskImageDTO createTaskImageDTO) {
        try {
            TaskImageDTO image = taskService.addTaskImage(taskId, 
                    createTaskImageDTO.imageUrl(), createTaskImageDTO.description(), createTaskImageDTO.publicId());
            return ResponseEntity.status(HttpStatus.CREATED).body(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{taskId}/images")
    public ResponseEntity<List<TaskImageDTO>> getTaskImages(@PathVariable Long taskId) {
        List<TaskImageDTO> images = taskService.getTaskImages(taskId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("{taskId}/images/{imageId}")
    public ResponseEntity<?> deleteTaskImage(@PathVariable Long imageId) {
        try {
            taskService.deleteTaskImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}

