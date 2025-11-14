package com.login.AxleXpert.Tasks.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Services.entity.Service;
import com.login.AxleXpert.Services.entity.ServiceSubTask;
import com.login.AxleXpert.Services.repository.ServiceSubTaskRepository;
import com.login.AxleXpert.Tasks.dto.CreateSubTaskDTO;
import com.login.AxleXpert.Tasks.dto.CreateTaskNoteDTO;
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
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.Vehicals.repository.VehicleRepository;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.bookings.repository.BookingRepository;
import com.login.AxleXpert.common.enums.NoteType;
import com.login.AxleXpert.common.enums.TaskStatus;
import com.login.AxleXpert.notifications.service.NotificationService;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for TaskService
 * 
 * Learning Focus:
 * - Testing complex business logic with multiple dependencies
 * - Testing cascading operations (task creation creates subtasks)
 * - Testing entity relationships (task -> subtasks -> notes)
 * - Testing status transitions and validations
 * - Using @Nested for logical test organization
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubTaskRepository subTaskRepository;

    @Mock
    private TaskNoteRepository taskNoteRepository;

    @Mock
    private TaskImageRepository taskImageRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceSubTaskRepository serviceSubTaskRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private NotificationService notificationService;

    private User testEmployee;
    private User testCustomer;
    private Service testService;
    private Branch testBranch;
    private Booking testBooking;
    private Task testTask;
    private SubTask testSubTask;

    @BeforeEach
    void setUp() {
        // Create service using constructor injection (much cleaner!)
        taskService = new TaskService(
            taskRepository,
            subTaskRepository,
            taskNoteRepository,
            taskImageRepository,
            bookingRepository,
            userRepository,
            serviceSubTaskRepository,
            vehicleRepository,
            notificationService
        );

        // Create test data using our TestDataBuilder
        testEmployee = TestDataBuilder.createEmployee();
        testCustomer = TestDataBuilder.createCustomer();
        testService = TestDataBuilder.createService();
        testBranch = TestDataBuilder.createBranch();
        testBooking = TestDataBuilder.createBooking(testCustomer, testService, testBranch);
        
        // Create test task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setBooking(testBooking);
        testTask.setAssignedEmployee(testEmployee);
        testTask.setServiceId(testService.getId());
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.NOT_STARTED);
        testTask.setEstimatedTimeMinutes(60);
        testTask.setStartTime(LocalDateTime.now());

        // Create test subtask
        testSubTask = new SubTask();
        testSubTask.setId(1L);
        testSubTask.setTask(testTask);
        testSubTask.setTitle("Test SubTask");
        testSubTask.setDescription("SubTask Description");
        testSubTask.setStatus(TaskStatus.NOT_STARTED);
    }

    @Nested
    @DisplayName("Create Task Tests")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task for booking when booking exists")
        void shouldCreateTask_whenBookingExists() {
            // ARRANGE
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(taskRepository.findByBookingId(testBooking.getId()))
                .thenReturn(Optional.empty()); // No existing task
            when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);
            when(serviceSubTaskRepository.findByServiceIdOrderByOrderIndexAsc(testService.getId()))
                .thenReturn(Collections.emptyList()); // No predefined subtasks
            
            // ACT
            TaskDTO result = taskService.createTaskForBooking(testBooking.getId(), testEmployee.getId());
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(any(Task.class));
            verify(bookingRepository, times(1)).findById(testBooking.getId());
            verify(userRepository, times(1)).findById(testEmployee.getId());
        }

        @Test
        @DisplayName("Should update existing task when task already exists for booking")
        void shouldUpdateExistingTask_whenTaskExists() {
            // ARRANGE
            User newEmployee = TestDataBuilder.createEmployee();
            newEmployee.setId(99L);
            newEmployee.setUsername("newemployee");

            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(newEmployee.getId()))
                .thenReturn(Optional.of(newEmployee));
            when(taskRepository.findByBookingId(testBooking.getId()))
                .thenReturn(Optional.of(testTask)); // Existing task found
            when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);
            
            // ACT
            TaskDTO result = taskService.createTaskForBooking(testBooking.getId(), newEmployee.getId());
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(testTask);
            verify(taskRepository, never()).save(argThat(task -> task.getId() == null)); // Not saving new task
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void shouldThrowException_whenBookingNotFound() {
            // ARRANGE
            when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> taskService.createTaskForBooking(999L, testEmployee.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
        }

        @Test
        @DisplayName("Should throw exception when employee not found")
        void shouldThrowException_whenEmployeeNotFound() {
            // ARRANGE
            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> taskService.createTaskForBooking(testBooking.getId(), 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found");
        }

        @Test
        @DisplayName("Should auto-create subtasks from service template")
        void shouldAutoCreateSubtasks_whenServiceHasTemplate() {
            // ARRANGE
            ServiceSubTask serviceSubTask1 = new ServiceSubTask();
            serviceSubTask1.setId(1L);
            serviceSubTask1.setTitle("Check oil");
            serviceSubTask1.setDescription("Verify oil level");
            serviceSubTask1.setService(testService);

            ServiceSubTask serviceSubTask2 = new ServiceSubTask();
            serviceSubTask2.setId(2L);
            serviceSubTask2.setTitle("Check brakes");
            serviceSubTask2.setService(testService);

            when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Optional.of(testBooking));
            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(taskRepository.findByBookingId(testBooking.getId()))
                .thenReturn(Optional.empty());
            when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);
            when(serviceSubTaskRepository.findByServiceIdOrderByOrderIndexAsc(testService.getId()))
                .thenReturn(Arrays.asList(serviceSubTask1, serviceSubTask2));
            when(subTaskRepository.save(any(SubTask.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
            
            // ACT
            TaskDTO result = taskService.createTaskForBooking(testBooking.getId(), testEmployee.getId());
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(any(Task.class));
            verify(subTaskRepository, times(2)).save(any(SubTask.class)); // 2 subtasks created
        }
    }

    @Nested
    @DisplayName("Get Task Tests")
    class GetTaskTests {

        @Test
        @DisplayName("Should return task when task exists")
        void shouldReturnTask_whenTaskExists() {
            // ARRANGE
            when(taskRepository.findById(testTask.getId()))
                .thenReturn(Optional.of(testTask));
            
            // ACT
            Optional<TaskDTO> result = taskService.getTaskById(testTask.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            verify(taskRepository, times(1)).findById(testTask.getId());
        }

        @Test
        @DisplayName("Should return empty when task not found")
        void shouldReturnEmpty_whenTaskNotFound() {
            // ARRANGE
            when(taskRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<TaskDTO> result = taskService.getTaskById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return task by booking ID")
        void shouldReturnTask_byBookingId() {
            // ARRANGE
            when(taskRepository.findByBookingId(testBooking.getId()))
                .thenReturn(Optional.of(testTask));
            
            // ACT
            Optional<TaskDTO> result = taskService.getTaskByBookingId(testBooking.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            verify(taskRepository, times(1)).findByBookingId(testBooking.getId());
        }

        @Test
        @DisplayName("Should return tasks by employee")
        void shouldReturnTasks_byEmployee() {
            // ARRANGE
            when(taskRepository.findByAssignedEmployeeIdWithBooking(testEmployee.getId()))
                .thenReturn(Arrays.asList(testTask));
            
            // ACT
            var result = taskService.getTasksByEmployee(testEmployee.getId());
            
            // ASSERT
            assertThat(result).isNotEmpty();
            verify(taskRepository, times(1)).findByAssignedEmployeeIdWithBooking(testEmployee.getId());
        }
    }

    @Nested
    @DisplayName("Update Task Tests")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task successfully")
        void shouldUpdateTask_successfully() {
            // ARRANGE
            // UpdateTaskDTO is a record: (TaskStatus status, LocalDateTime startTime, LocalDateTime completedTime)
            UpdateTaskDTO updateDTO = new UpdateTaskDTO(
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now(),
                null
            );

            when(taskRepository.findById(testTask.getId()))
                .thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);
            
            // ACT
            TaskDTO result = taskService.updateTask(testTask.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(testTask);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent task")
        void shouldThrowException_whenTaskNotFound() {
            // ARRANGE
            UpdateTaskDTO updateDTO = new UpdateTaskDTO(
                TaskStatus.COMPLETED,
                null,
                LocalDateTime.now()
            );

            when(taskRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> taskService.updateTask(999L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
        }
    }

    @Nested
    @DisplayName("SubTask Tests")
    class SubTaskTests {

        @Test
        @DisplayName("Should add subtask to task")
        void shouldAddSubtask_successfully() {
            // ARRANGE
            // CreateSubTaskDTO is a record: (String title, String description, Integer orderIndex)
            CreateSubTaskDTO createDTO = new CreateSubTaskDTO(
                "New SubTask",
                "SubTask Description",
                1
            );

            when(taskRepository.findById(testTask.getId()))
                .thenReturn(Optional.of(testTask));
            when(subTaskRepository.save(any(SubTask.class)))
                .thenReturn(testSubTask);
            
            // ACT
            SubTaskDTO result = taskService.addSubTask(testTask.getId(), createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(subTaskRepository, times(1)).save(any(SubTask.class));
        }

        @Test
        @DisplayName("Should update subtask status")
        void shouldUpdateSubtask_successfully() {
            // ARRANGE
            // UpdateSubTaskDTO is a record: (String title, String description, TaskStatus status, String notes, Integer orderIndex)
            UpdateSubTaskDTO updateDTO = new UpdateSubTaskDTO(
                "Updated Title",
                "Completed subtask",
                TaskStatus.COMPLETED,
                "Completed notes",
                1
            );

            when(subTaskRepository.findById(testSubTask.getId()))
                .thenReturn(Optional.of(testSubTask));
            when(subTaskRepository.save(any(SubTask.class)))
                .thenReturn(testSubTask);
            
            // ACT
            SubTaskDTO result = taskService.updateSubTask(testSubTask.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(subTaskRepository, times(1)).save(testSubTask);
        }

        @Test
        @DisplayName("Should delete subtask")
        void shouldDeleteSubtask_successfully() {
            // ARRANGE
            when(subTaskRepository.findById(testSubTask.getId()))
                .thenReturn(Optional.of(testSubTask));
            doNothing().when(subTaskRepository).delete(any(SubTask.class));
            
            // ACT
            taskService.deleteSubTask(testSubTask.getId());
            
            // ASSERT
            verify(subTaskRepository, times(1)).delete(testSubTask);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent subtask")
        void shouldThrowException_whenDeletingNonExistentSubtask() {
            // ARRANGE
            when(subTaskRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> taskService.deleteSubTask(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SubTask not found");
        }
    }

    @Nested
    @DisplayName("Task Note Tests")
    class TaskNoteTests {

        @Test
        @DisplayName("Should add note to task")
        void shouldAddNote_successfully() {
            // ARRANGE
            // CreateTaskNoteDTO is a record: (NoteType noteType, String content, boolean visibleToCustomer)
            CreateTaskNoteDTO createDTO = new CreateTaskNoteDTO(
                NoteType.EMPLOYEE_NOTE,
                "Important note",
                true
            );

            TaskNote taskNote = new TaskNote();
            taskNote.setId(1L);
            taskNote.setTask(testTask);
            taskNote.setContent("Important note");  // Fixed: use setContent() not setNote()
            taskNote.setNoteType(NoteType.EMPLOYEE_NOTE);
            taskNote.setAuthor(testEmployee);

            when(taskRepository.findById(testTask.getId()))
                .thenReturn(Optional.of(testTask));
            when(userRepository.findById(testEmployee.getId()))
                .thenReturn(Optional.of(testEmployee));
            when(taskNoteRepository.save(any(TaskNote.class)))
                .thenReturn(taskNote);
            
            // ACT
            TaskNoteDTO result = taskService.addTaskNote(testTask.getId(), testEmployee.getId(), createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.content()).isEqualTo("Important note");  // Fixed: use content() accessor
            verify(taskNoteRepository, times(1)).save(any(TaskNote.class));
        }

        @Test
        @DisplayName("Should get task notes by type")
        void shouldGetNotes_byType() {
            // ARRANGE
            TaskNote note1 = new TaskNote();
            note1.setId(1L);
            note1.setContent("Technician note");  // Fixed: use setContent()
            note1.setNoteType(NoteType.EMPLOYEE_NOTE);
            note1.setTask(testTask);
            note1.setAuthor(testEmployee);

            // Fixed: use correct repository method name with OrderBy
            when(taskNoteRepository.findByTaskIdAndNoteTypeOrderByCreatedAtDesc(testTask.getId(), NoteType.EMPLOYEE_NOTE))
                .thenReturn(Arrays.asList(note1));
            
            // ACT
            List<TaskNoteDTO> result = taskService.getTaskNotes(testTask.getId(), NoteType.EMPLOYEE_NOTE);
            
            // ASSERT
            assertThat(result).hasSize(1);
            assertThat(result.get(0).noteType()).isEqualTo(NoteType.EMPLOYEE_NOTE);
        }

        @Test
        @DisplayName("Should delete task note")
        void shouldDeleteNote_successfully() {
            // ARRANGE
            TaskNote note = new TaskNote();
            note.setId(1L);
            note.setContent("Note to delete");  // Fixed: use setContent()

            when(taskNoteRepository.findById(note.getId()))
                .thenReturn(Optional.of(note));
            doNothing().when(taskNoteRepository).delete(any(TaskNote.class));
            
            // ACT
            taskService.deleteTaskNote(note.getId());
            
            // ASSERT
            verify(taskNoteRepository, times(1)).delete(note);
        }
    }

    @Nested
    @DisplayName("Task Image Tests")
    class TaskImageTests {

        @Test
        @DisplayName("Should add image to task")
        void shouldAddImage_successfully() {
            // ARRANGE
            TaskImage taskImage = new TaskImage();
            taskImage.setId(1L);
            taskImage.setTask(testTask);
            taskImage.setImageUrl("https://example.com/image.jpg");
            taskImage.setDescription("Test image");
            taskImage.setPublicId("public123");

            when(taskRepository.findById(testTask.getId()))
                .thenReturn(Optional.of(testTask));
            when(taskImageRepository.save(any(TaskImage.class)))
                .thenReturn(taskImage);
            
            // ACT
            TaskImageDTO result = taskService.addTaskImage(
                testTask.getId(),
                "https://example.com/image.jpg",
                "Test image",
                "public123"
            );
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.imageUrl()).isEqualTo("https://example.com/image.jpg");
            verify(taskImageRepository, times(1)).save(any(TaskImage.class));
        }

        @Test
        @DisplayName("Should get task images")
        void shouldGetImages_successfully() {
            // ARRANGE
            TaskImage image1 = new TaskImage();
            image1.setId(1L);
            image1.setImageUrl("https://example.com/image1.jpg");
            image1.setTask(testTask);

            // Fixed: use correct repository method name with OrderBy
            when(taskImageRepository.findByTaskIdOrderByCreatedAtDesc(testTask.getId()))
                .thenReturn(Arrays.asList(image1));
            
            // ACT
            List<TaskImageDTO> result = taskService.getTaskImages(testTask.getId());
            
            // ASSERT
            assertThat(result).hasSize(1);
            assertThat(result.get(0).imageUrl()).isEqualTo("https://example.com/image1.jpg");
        }

        @Test
        @DisplayName("Should delete task image")
        void shouldDeleteImage_successfully() {
            // ARRANGE
            TaskImage image = new TaskImage();
            image.setId(1L);

            when(taskImageRepository.findById(image.getId()))
                .thenReturn(Optional.of(image));
            doNothing().when(taskImageRepository).delete(any(TaskImage.class));
            
            // ACT
            taskService.deleteTaskImage(image.getId());
            
            // ASSERT
            verify(taskImageRepository, times(1)).delete(image);
        }
    }
}
