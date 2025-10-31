package com.login.AxleXpert.Tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Tasks.entity.TaskImage;

@Repository
public interface TaskImageRepository extends JpaRepository<TaskImage, Long> {
    List<TaskImage> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}