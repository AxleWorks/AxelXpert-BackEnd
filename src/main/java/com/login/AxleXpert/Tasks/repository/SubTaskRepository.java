package com.login.AxleXpert.Tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Tasks.entity.SubTask;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    List<SubTask> findByTaskIdOrderByOrderIndexAsc(Long taskId);
    List<SubTask> findByTaskId(Long taskId);
}
