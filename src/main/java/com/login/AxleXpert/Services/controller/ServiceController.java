package com.login.AxleXpert.Services.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.Services.dto.CreateServiceDTO;
import com.login.AxleXpert.Services.dto.CreateServiceSubTaskDTO;
import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Services.dto.ServiceSubTaskDTO;
import com.login.AxleXpert.Services.dto.UpdateServiceDTO;
import com.login.AxleXpert.Services.dto.UpdateServiceSubTaskDTO;
import com.login.AxleXpert.Services.service.ServiceService;
import com.login.AxleXpert.Services.service.ServiceSubTaskService;
import com.login.AxleXpert.common.dto.ErrorResponse;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;
    private final ServiceSubTaskService serviceSubTaskService;

    @Autowired
    public ServiceController(ServiceService serviceService, ServiceSubTaskService serviceSubTaskService) {
        this.serviceService = serviceService;
        this.serviceSubTaskService = serviceSubTaskService;
    }

    // ==================== Service CRUD Operations ====================
    
    @GetMapping("")
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        List<ServiceDTO> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        Optional<ServiceDTO> service = serviceService.getServiceById(id);
        if (service.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Service not found with id: " + id));
        }
        return ResponseEntity.ok(service.get());
    }

    @PostMapping("")
    public ResponseEntity<?> createService(@RequestBody CreateServiceDTO dto) {
        try {
            ServiceDTO created = serviceService.createService(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, 
                                          @RequestBody UpdateServiceDTO dto) {
        try {
            ServiceDTO updated = serviceService.updateService(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== Service SubTask Management ====================
    
    @PostMapping("/{serviceId}/subtasks")
    public ResponseEntity<?> addSubTaskToService(@PathVariable Long serviceId,
                                                 @RequestBody CreateServiceSubTaskDTO dto) {
        try {
            ServiceSubTaskDTO subTask = serviceSubTaskService.addSubTaskToService(serviceId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(subTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{serviceId}/subtasks")
    public ResponseEntity<List<ServiceSubTaskDTO>> getServiceSubTasks(@PathVariable Long serviceId) {
        List<ServiceSubTaskDTO> subTasks = serviceSubTaskService.getSubTasksByServiceId(serviceId);
        return ResponseEntity.ok(subTasks);
    }

    @PatchMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> updateServiceSubTask(@PathVariable Long subTaskId,
                                                  @RequestBody UpdateServiceSubTaskDTO dto) {
        try {
            ServiceSubTaskDTO updated = serviceSubTaskService.updateServiceSubTask(subTaskId, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/subtasks/{subTaskId}")
    public ResponseEntity<?> deleteServiceSubTask(@PathVariable Long subTaskId) {
        try {
            serviceSubTaskService.deleteServiceSubTask(subTaskId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}

