package com.login.AxleXpert.Tasks.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.bookings.entity.Booking;
import com.login.AxleXpert.common.enums.TaskStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"subTasks", "taskNotes", "taskImages"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id", nullable = false)
    private User assignedEmployee;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskNote> taskNotes = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskImage> taskImages = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to calculate overall task status based on subtasks
    public TaskStatus calculateOverallStatus() {
        if (subTasks.isEmpty()) {
            return status;
        }

        boolean allCompleted = subTasks.stream().allMatch(st -> st.getStatus() == TaskStatus.COMPLETED);
        boolean anyInProgress = subTasks.stream().anyMatch(st -> st.getStatus() == TaskStatus.IN_PROGRESS);
        boolean anyOnHold = subTasks.stream().anyMatch(st -> st.getStatus() == TaskStatus.ON_HOLD);

        if (allCompleted) {
            return TaskStatus.COMPLETED;
        } else if (anyOnHold) {
            return TaskStatus.ON_HOLD;
        } else if (anyInProgress) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.NOT_STARTED;
        }
    }
}
