package com.login.AxleXpert.Tasks.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;
import com.login.AxleXpert.bookings.Booking;
import com.login.AxleXpert.bookings.BookingRepository;
import com.login.AxleXpert.Tasks.dto.CreateSubTaskDTO;
import com.login.AxleXpert.Tasks.dto.CreateTaskNoteDTO;
import com.login.AxleXpert.Tasks.dto.EmployeeTaskDTO;
import com.login.AxleXpert.Tasks.dto.SubTaskDTO;
import com.login.AxleXpert.Tasks.dto.TaskDTO;
import com.login.AxleXpert.Tasks.dto.TaskImageDTO;
import com.login.AxleXpert.Tasks.dto.TaskNoteDTO;
import com.login.AxleXpert.Tasks.dto.UpdateSubTaskDTO;
import com.login.AxleXpert.Tasks.dto.UpdateTaskDTO;
import com.login.AxleXpert.Tasks.entity.SubTask;
import com.login.AxleXpert.Tasks.entity.Task;
import com.login.AxleXpert.Tasks.entity.TaskImage;
import com.login.AxleXpert.Tasks.entity.TaskNote;
import com.login.AxleXpert.Tasks.repository.SubTaskRepository;
import com.login.AxleXpert.Tasks.repository.TaskImageRepository;
import com.login.AxleXpert.Tasks.repository.TaskNoteRepository;
import com.login.AxleXpert.Tasks.repository.TaskRepository;
import com.login.AxleXpert.common.enums.NoteType;
import com.login.AxleXpert.common.enums.TaskStatus;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskNoteRepository taskNoteRepository;
    private final TaskImageRepository taskImageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, 
                      SubTaskRepository subTaskRepository,
                      TaskNoteRepository taskNoteRepository, 
                      TaskImageRepository taskImageRepository,
                      BookingRepository bookingRepository, 
                      UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.taskNoteRepository = taskNoteRepository;
        this.taskImageRepository = taskImageRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a task automatically when an employee is assigned to a booking
     */
    public TaskDTO createTaskForBooking(Long bookingId, Long employeeId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with id: " + bookingId);
        }

        Optional<User> employeeOpt = userRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with id: " + employeeId);
        }

        Booking booking = bookingOpt.get();
        User employee = employeeOpt.get();

        // Check if task already exists for this booking
        Optional<Task> existingTask = taskRepository.findByBookingId(bookingId);
        if (existingTask.isPresent()) {
            // Update the existing task with new employee assignment
            Task task = existingTask.get();
            task.setAssignedEmployee(employee);
            Task savedTask = taskRepository.save(task);
            return toTaskDTO(savedTask);
        }

        // Create new task
        Task task = new Task();
        task.setBooking(booking);
        task.setAssignedEmployee(employee);
        task.setTitle("Service Task - " + booking.getService().getName());
        task.setDescription("Complete the " + booking.getService().getName() + " service for customer: " + booking.getCustomerName());
        task.setStatus(TaskStatus.NOT_STARTED);

        Task savedTask = taskRepository.save(task);
        return toTaskDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTaskDTO> getTasksByEmployee(Long employeeId) {
        List<Task> tasks = taskRepository.findByAssignedEmployeeIdWithBooking(employeeId);
        return tasks.stream().map(this::toEmployeeTaskDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByCustomer(Long customerId) {
        List<Task> tasks = taskRepository.findByCustomerId(customerId);
        return tasks.stream().map(this::toTaskDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TaskDTO> getTaskById(Long taskId) {
        return taskRepository.findById(taskId).map(this::toTaskDTO);
    }

    @Transactional(readOnly = true)
    public Optional<TaskDTO> getTaskByBookingId(Long bookingId) {
        return taskRepository.findByBookingId(bookingId).map(this::toTaskDTO);
    }

    // public TaskDTO updateTaskStatus(Long taskId, TaskStatus status) {
    //     Optional<Task> taskOpt = taskRepository.findById(taskId);
    //     if (taskOpt.isEmpty()) {
    //         throw new IllegalArgumentException("Task not found with id: " + taskId);
    //     }

    //     Task task = taskOpt.get();
    //     task.setStatus(status);
    //     Task savedTask = taskRepository.save(task);
    //     return toTaskDTO(savedTask);
    // }

    public TaskDTO updateTask(Long taskId, UpdateTaskDTO updateTaskDTO) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        Task task = taskOpt.get();
        
        if (updateTaskDTO.status() != null) {
            task.setStatus(updateTaskDTO.status());
        }
        if (updateTaskDTO.startTime() != null) {
            task.setStartTime(updateTaskDTO.startTime());
        }
        if (updateTaskDTO.completedTime() != null) {
            task.setCompletedTime(updateTaskDTO.completedTime());
        }

        Task savedTask = taskRepository.save(task);
        return toTaskDTO(savedTask);
    }

    public SubTaskDTO addSubTask(Long taskId, CreateSubTaskDTO createSubTaskDTO) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        Task task = taskOpt.get();
        
        SubTask subTask = new SubTask();
        subTask.setTask(task);
        subTask.setTitle(createSubTaskDTO.title());
        subTask.setDescription(createSubTaskDTO.description());
        subTask.setOrderIndex(createSubTaskDTO.orderIndex());
        subTask.setStatus(TaskStatus.NOT_STARTED);

        SubTask savedSubTask = subTaskRepository.save(subTask);
        
        // Update parent task status
        updateParentTaskStatus(task);
        
        return toSubTaskDTO(savedSubTask);
    }

    public SubTaskDTO updateSubTask(Long subTaskId, UpdateSubTaskDTO updateSubTaskDTO) {
        Optional<SubTask> subTaskOpt = subTaskRepository.findById(subTaskId);
        if (subTaskOpt.isEmpty()) {
            throw new IllegalArgumentException("SubTask not found with id: " + subTaskId);
        }

        SubTask subTask = subTaskOpt.get();
        
        if (updateSubTaskDTO.title() != null) {
            subTask.setTitle(updateSubTaskDTO.title());
        }
        if (updateSubTaskDTO.description() != null) {
            subTask.setDescription(updateSubTaskDTO.description());
        }
        if (updateSubTaskDTO.status() != null) {
            subTask.setStatus(updateSubTaskDTO.status());
        }
        if (updateSubTaskDTO.notes() != null) {
            subTask.setNotes(updateSubTaskDTO.notes());
        }
        if (updateSubTaskDTO.orderIndex() != null) {
            subTask.setOrderIndex(updateSubTaskDTO.orderIndex());
        }

        SubTask savedSubTask = subTaskRepository.save(subTask);
        
        // Update parent task status
        updateParentTaskStatus(subTask.getTask());
        
        return toSubTaskDTO(savedSubTask);
    }

    public void deleteSubTask(Long subTaskId) {
        Optional<SubTask> subTaskOpt = subTaskRepository.findById(subTaskId);
        if (subTaskOpt.isEmpty()) {
            throw new IllegalArgumentException("SubTask not found with id: " + subTaskId);
        }

        SubTask subTask = subTaskOpt.get();
        Task parentTask = subTask.getTask();
        
        subTaskRepository.delete(subTask);
        
        // Update parent task status
        updateParentTaskStatus(parentTask);
    }

    public TaskNoteDTO addTaskNote(Long taskId, Long authorId, CreateTaskNoteDTO createTaskNoteDTO) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        Optional<User> authorOpt = userRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + authorId);
        }

        Task task = taskOpt.get();
        User author = authorOpt.get();

        TaskNote taskNote = new TaskNote();
        taskNote.setTask(task);
        taskNote.setAuthor(author);
        taskNote.setNoteType(createTaskNoteDTO.noteType());
        taskNote.setContent(createTaskNoteDTO.content());

        TaskNote savedNote = taskNoteRepository.save(taskNote);
        return toTaskNoteDTO(savedNote);
    }

    @Transactional(readOnly = true)
    public List<TaskNoteDTO> getTaskNotes(Long taskId, NoteType noteType) {
        List<TaskNote> notes;
        if (noteType != null) {
            notes = taskNoteRepository.findByTaskIdAndNoteTypeOrderByCreatedAtDesc(taskId, noteType);
        } else {
            notes = taskNoteRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        }
        return notes.stream().map(this::toTaskNoteDTO).collect(Collectors.toList());
    }

    public TaskImageDTO addTaskImage(Long taskId, String imageUrl, String description) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        Task task = taskOpt.get();
        
        TaskImage taskImage = new TaskImage();
        taskImage.setTask(task);
        taskImage.setImageUrl(imageUrl);
        taskImage.setDescription(description);

        TaskImage savedImage = taskImageRepository.save(taskImage);
        return toTaskImageDTO(savedImage);
    }

    @Transactional(readOnly = true)
    public List<TaskImageDTO> getTaskImages(Long taskId) {
        List<TaskImage> images = taskImageRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return images.stream().map(this::toTaskImageDTO).collect(Collectors.toList());
    }

    public void deleteTaskImage(Long imageId) {
        Optional<TaskImage> imageOpt = taskImageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            throw new IllegalArgumentException("Image not found with id: " + imageId);
        }

        taskImageRepository.delete(imageOpt.get());
    }

    private void updateParentTaskStatus(Task task) {
        TaskStatus calculatedStatus = task.calculateOverallStatus();
        task.setStatus(calculatedStatus);
        taskRepository.save(task);
    }

    private TaskDTO toTaskDTO(Task task) {
        List<SubTaskDTO> subTasks = task.getSubTasks().stream()
                .map(this::toSubTaskDTO)
                .collect(Collectors.toList());

        List<TaskNoteDTO> taskNotes = task.getTaskNotes().stream()
                .map(this::toTaskNoteDTO)
                .collect(Collectors.toList());

        List<TaskImageDTO> taskImages = task.getTaskImages().stream()
                .map(this::toTaskImageDTO)
                .collect(Collectors.toList());

        return new TaskDTO(
                task.getId(),
                task.getBooking().getId(),
                task.getAssignedEmployee().getId(),
                task.getAssignedEmployee().getUsername(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.calculateOverallStatus(),
                subTasks,
                taskNotes,
                taskImages,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private EmployeeTaskDTO toEmployeeTaskDTO(Task task) {
        return new EmployeeTaskDTO(
                task.getId(),
                task.getBooking().getCustomerName(),
                task.getBooking().getVehicle(),
                task.getBooking().getService().getDurationMinutes(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getSubTasks().stream()
                        .map(this::toSubTaskDTO)
                        .collect(Collectors.toList()),
                task.getStartTime(),
                task.getCompletedTime(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private SubTaskDTO toSubTaskDTO(SubTask subTask) {
        return new SubTaskDTO(
                subTask.getId(),
                subTask.getTask().getId(),
                subTask.getTitle(),
                subTask.getDescription(),
                subTask.getStatus(),
                subTask.getOrderIndex(),
                subTask.getNotes(),
                subTask.getCreatedAt(),
                subTask.getUpdatedAt()
        );
    }

    private TaskNoteDTO toTaskNoteDTO(TaskNote taskNote) {
        return new TaskNoteDTO(
                taskNote.getId(),
                taskNote.getTask().getId(),
                taskNote.getAuthor().getId(),
                taskNote.getAuthor().getUsername(),
                taskNote.getNoteType(),
                taskNote.getContent(),
                taskNote.getCreatedAt()
        );
    }

    private TaskImageDTO toTaskImageDTO(TaskImage taskImage) {
        return new TaskImageDTO(
                taskImage.getId(),
                taskImage.getTask().getId(),
                taskImage.getImageUrl(),
                taskImage.getDescription(),
                taskImage.getCreatedAt()
        );
    }
}