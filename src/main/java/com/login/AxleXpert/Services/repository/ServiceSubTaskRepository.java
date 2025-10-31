package com.login.AxleXpert.Services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Services.entity.ServiceSubTask;

@Repository
public interface ServiceSubTaskRepository extends JpaRepository<ServiceSubTask, Long> {
    List<ServiceSubTask> findByServiceIdOrderByOrderIndexAsc(Long serviceId);
}
