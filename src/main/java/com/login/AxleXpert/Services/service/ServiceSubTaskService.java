package com.login.AxleXpert.Services.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Services.dto.CreateServiceSubTaskDTO;
import com.login.AxleXpert.Services.dto.ServiceSubTaskDTO;
import com.login.AxleXpert.Services.dto.UpdateServiceSubTaskDTO;
import com.login.AxleXpert.Services.entity.ServiceSubTask;
import com.login.AxleXpert.Services.repository.ServiceRepository;
import com.login.AxleXpert.Services.repository.ServiceSubTaskRepository;

@Service
@Transactional
public class ServiceSubTaskService {

    private final ServiceSubTaskRepository serviceSubTaskRepository;
    private final ServiceRepository serviceRepository;

    public ServiceSubTaskService(ServiceSubTaskRepository serviceSubTaskRepository,
                                ServiceRepository serviceRepository) {
        this.serviceSubTaskRepository = serviceSubTaskRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Add a predefined subtask template to a service
     */
    public ServiceSubTaskDTO addSubTaskToService(Long serviceId, CreateServiceSubTaskDTO dto) {
        com.login.AxleXpert.Services.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + serviceId));

        ServiceSubTask serviceSubTask = new ServiceSubTask();
        serviceSubTask.setService(service);
        serviceSubTask.setTitle(dto.title());
        serviceSubTask.setDescription(dto.description());
        serviceSubTask.setOrderIndex(dto.orderIndex());
        serviceSubTask.setIsMandatory(dto.isMandatory() != null && dto.isMandatory());

        ServiceSubTask saved = serviceSubTaskRepository.save(serviceSubTask);
        return toDTO(saved);
    }

    /**
     * Get all predefined subtasks for a service
     */
    @Transactional(readOnly = true)
    public List<ServiceSubTaskDTO> getSubTasksByServiceId(Long serviceId) {
        List<ServiceSubTask> subTasks = serviceSubTaskRepository.findByServiceIdOrderByOrderIndexAsc(serviceId);
        return subTasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update a service subtask template
     */
    public ServiceSubTaskDTO updateServiceSubTask(Long subTaskId, UpdateServiceSubTaskDTO dto) {
        ServiceSubTask subTask = serviceSubTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Service SubTask not found with id: " + subTaskId));

        if (dto.title() != null) {
            subTask.setTitle(dto.title());
        }
        if (dto.description() != null) {
            subTask.setDescription(dto.description());
        }
        if (dto.orderIndex() != null) {
            subTask.setOrderIndex(dto.orderIndex());
        }
        if (dto.isMandatory() != null) {
            subTask.setIsMandatory(dto.isMandatory());
        }

        ServiceSubTask updated = serviceSubTaskRepository.save(subTask);
        return toDTO(updated);
    }

    /**
     * Delete a service subtask template
     */
    public void deleteServiceSubTask(Long subTaskId) {
        ServiceSubTask subTask = serviceSubTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Service SubTask not found with id: " + subTaskId));
        serviceSubTaskRepository.delete(subTask);
    }

    @Transactional(readOnly = true)
    public Optional<ServiceSubTaskDTO> getServiceSubTaskById(Long subTaskId) {
        return serviceSubTaskRepository.findById(subTaskId)
                .map(this::toDTO);
    }

    private ServiceSubTaskDTO toDTO(ServiceSubTask entity) {
        return new ServiceSubTaskDTO(
                entity.getId(),
                entity.getService().getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getOrderIndex(),
                entity.getIsMandatory(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
