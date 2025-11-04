package com.login.AxleXpert.chatbot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.login.AxleXpert.Branches.dto.BranchDTO;
import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Tasks.dto.ProgressTrackingDTO;
import com.login.AxleXpert.Tasks.dto.TaskDTO;
import com.login.AxleXpert.Users.dto.UserDTO;
import com.login.AxleXpert.Vehicals.dto.VehicleDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for calling internal backend API endpoints
 * Provides methods to fetch data from various services for chatbot responses
 */
@Service
@Slf4j
public class BackendApiService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080"; // Adjust if needed

    @Autowired
    public BackendApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Create HTTP headers with authorization if token is provided
     */
    private org.springframework.http.HttpHeaders createHeaders(String accessToken) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            headers.set("Authorization", "Bearer " + accessToken);
        }
        return headers;
    }

    /**
     * Get all available services
     */
    public List<ServiceDTO> getAllServices(String accessToken) {
        try {
            String url = baseUrl + "/api/services";
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<ServiceDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ServiceDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching services: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all branches
     */
    public List<BranchDTO> getAllBranches(String accessToken) {
        try {
            String url = baseUrl + "/api/branches/all";
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<BranchDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BranchDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching branches: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get all managers
     */
    public List<UserDTO> getAllManagers(String accessToken) {
        try {
            String url = baseUrl + "/api/users/managers";
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching managers: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get user's vehicles
     */
    public List<VehicleDTO> getUserVehicles(Long userId, String accessToken) {
        try {
            String url = baseUrl + "/api/vehicles/user/" + userId;
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<VehicleDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<VehicleDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching user vehicles: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get customer's tasks (in-progress services)
     */
    public List<TaskDTO> getCustomerTasks(Long customerId, String accessToken) {
        try {
            String url = baseUrl + "/api/tasks/customer/" + customerId;
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<TaskDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<TaskDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching customer tasks: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get customer's progress tracking
     */
    public List<ProgressTrackingDTO> getCustomerProgressTracking(Long customerId, String accessToken) {
        try {
            String url = baseUrl + "/api/tasks/customer/" + customerId + "/progress-tracking";
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<ProgressTrackingDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ProgressTrackingDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching customer progress tracking: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get employees by branch
     */
    public List<UserDTO> getEmployeesByBranch(Long branchId, String accessToken) {
        try {
            String url = baseUrl + "/api/users/branch/" + branchId + "/employees";
            org.springframework.http.HttpHeaders headers = createHeaders(accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching employees by branch: {}", e.getMessage());
            return List.of();
        }
    }
}