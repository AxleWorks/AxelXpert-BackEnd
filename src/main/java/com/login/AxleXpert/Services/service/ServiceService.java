package com.login.AxleXpert.Services.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Services.dto.CreateServiceDTO;
import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Services.dto.UpdateServiceDTO;
import com.login.AxleXpert.Services.repository.ServiceRepository;

@Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Get all services
     */
    @Transactional(readOnly = true)
    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(ServiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get service by ID
     */
    @Transactional(readOnly = true)
    public Optional<ServiceDTO> getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(ServiceDTO::fromEntity);
    }

    /**
     * Create a new service
     */
    public ServiceDTO createService(CreateServiceDTO dto) {
        com.login.AxleXpert.Services.entity.Service service = 
                new com.login.AxleXpert.Services.entity.Service();
        
        service.setName(dto.name());
        service.setPrice(dto.price());
        service.setDurationMinutes(dto.durationMinutes());
        service.setDescription(dto.description());

        com.login.AxleXpert.Services.entity.Service saved = serviceRepository.save(service);
        return ServiceDTO.fromEntity(saved);
    }

    /**
     * Update an existing service
     */
    public ServiceDTO updateService(Long id, UpdateServiceDTO dto) {
        com.login.AxleXpert.Services.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + id));

        if (dto.name() != null) {
            service.setName(dto.name());
        }
        if (dto.price() != null) {
            service.setPrice(dto.price());
        }
        if (dto.durationMinutes() != null) {
            service.setDurationMinutes(dto.durationMinutes());
        }
        if (dto.description() != null) {
            service.setDescription(dto.description());
        }

        com.login.AxleXpert.Services.entity.Service updated = serviceRepository.save(service);
        return ServiceDTO.fromEntity(updated);
    }

    /**
     * Delete a service
     */
    public void deleteService(Long id) {
        com.login.AxleXpert.Services.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + id));
        
        serviceRepository.delete(service);
    }
}
