package com.login.AxleXpert.Services.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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

import com.login.AxleXpert.Services.dto.CreateServiceDTO;
import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Services.dto.UpdateServiceDTO;
import com.login.AxleXpert.Services.entity.Service;
import com.login.AxleXpert.Services.repository.ServiceRepository;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for ServiceService
 * 
 * Learning Focus:
 * - Testing CRUD operations for service catalog management
 * - Testing DTO conversions (fromEntity patterns)
 * - Testing business validation rules
 * - Testing BigDecimal price handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceService Unit Tests")
class ServiceServiceTest {

    private ServiceService serviceService;

    @Mock
    private ServiceRepository serviceRepository;

    private Service testService;

    @BeforeEach
    void setUp() {
        serviceService = new ServiceService(serviceRepository);
        testService = TestDataBuilder.createService();
    }

    @Nested
    @DisplayName("Get Service Tests")
    class GetServiceTests {

        @Test
        @DisplayName("Should return all services")
        void shouldReturnAllServices() {
            // ARRANGE
            Service service2 = TestDataBuilder.createService();
            service2.setId(2L);
            service2.setName("Brake Service");

            when(serviceRepository.findAll())
                .thenReturn(Arrays.asList(testService, service2));
            
            // ACT
            List<ServiceDTO> result = serviceService.getAllServices();
            
            // ASSERT
            assertThat(result).hasSize(2);
            verify(serviceRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no services exist")
        void shouldReturnEmptyList_whenNoServices() {
            // ARRANGE
            when(serviceRepository.findAll())
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<ServiceDTO> result = serviceService.getAllServices();
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return service by ID when exists")
        void shouldReturnService_whenExists() {
            // ARRANGE
            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            
            // ACT
            Optional<ServiceDTO> result = serviceService.getServiceById(testService.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(testService.getName());
            verify(serviceRepository, times(1)).findById(testService.getId());
        }

        @Test
        @DisplayName("Should return empty when service not found")
        void shouldReturnEmpty_whenServiceNotFound() {
            // ARRANGE
            when(serviceRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<ServiceDTO> result = serviceService.getServiceById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Create Service Tests")
    class CreateServiceTests {

        @Test
        @DisplayName("Should create service successfully")
        void shouldCreateService_successfully() {
            // ARRANGE
            CreateServiceDTO createDTO = new CreateServiceDTO(
                "New Service",
                new BigDecimal("150.00"),
                60,
                "Description of new service"
            );

            Service savedService = new Service();
            savedService.setId(1L);
            savedService.setName(createDTO.name());
            savedService.setDescription(createDTO.description());
            savedService.setPrice(createDTO.price());
            savedService.setDurationMinutes(createDTO.durationMinutes());

            when(serviceRepository.save(any(Service.class)))
                .thenReturn(savedService);
            
            // ACT
            ServiceDTO result = serviceService.createService(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("New Service");
            verify(serviceRepository, times(1)).save(any(Service.class));
        }

        @Test
        @DisplayName("Should create service with null description")
        void shouldCreateService_withNullDescription() {
            // ARRANGE
            CreateServiceDTO createDTO = new CreateServiceDTO(
                "Service Without Description",
                new BigDecimal("100.00"),
                45,
                null  // No description
            );

            Service savedService = new Service();
            savedService.setId(1L);
            savedService.setName(createDTO.name());
            savedService.setPrice(createDTO.price());

            when(serviceRepository.save(any(Service.class)))
                .thenReturn(savedService);
            
            // ACT
            ServiceDTO result = serviceService.createService(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(serviceRepository, times(1)).save(any(Service.class));
        }
    }

    @Nested
    @DisplayName("Update Service Tests")
    class UpdateServiceTests {

        @Test
        @DisplayName("Should update service successfully")
        void shouldUpdateService_successfully() {
            // ARRANGE
            UpdateServiceDTO updateDTO = new UpdateServiceDTO(
                "Updated Service Name",
                new BigDecimal("200.00"),
                90,
                "Updated description"
            );

            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class)))
                .thenReturn(testService);
            
            // ACT
            ServiceDTO result = serviceService.updateService(testService.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(serviceRepository, times(1)).save(testService);
            verify(serviceRepository, times(1)).findById(testService.getId());
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent service")
        void shouldThrowException_whenServiceNotFound() {
            // ARRANGE
            UpdateServiceDTO updateDTO = new UpdateServiceDTO(
                "Updated Name",
                new BigDecimal("150.00"),
                60,
                "Updated description"
            );

            when(serviceRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> serviceService.updateService(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service not found");
        }

        @Test
        @DisplayName("Should update service with partial data")
        void shouldUpdateService_withPartialData() {
            // ARRANGE
            UpdateServiceDTO updateDTO = new UpdateServiceDTO(
                "Updated Name Only",
                testService.getPrice(),         // Keep existing
                testService.getDurationMinutes(), // Keep existing
                testService.getDescription()    // Keep existing
            );

            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            when(serviceRepository.save(any(Service.class)))
                .thenReturn(testService);
            
            // ACT
            ServiceDTO result = serviceService.updateService(testService.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(serviceRepository, times(1)).save(testService);
        }
    }

    @Nested
    @DisplayName("Delete Service Tests")
    class DeleteServiceTests {

        @Test
        @DisplayName("Should delete service successfully")
        void shouldDeleteService_successfully() {
            // ARRANGE
            when(serviceRepository.findById(testService.getId()))
                .thenReturn(Optional.of(testService));
            doNothing().when(serviceRepository).delete(testService);
            
            // ACT
            serviceService.deleteService(testService.getId());
            
            // ASSERT
            verify(serviceRepository, times(1)).findById(testService.getId());
            verify(serviceRepository, times(1)).delete(testService);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent service")
        void shouldThrowException_whenServiceNotFound() {
            // ARRANGE
            when(serviceRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> serviceService.deleteService(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service not found");
            
            verify(serviceRepository, never()).delete(any(Service.class));
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle BigDecimal price precision correctly")
        void shouldHandlePrice_withCorrectPrecision() {
            // ARRANGE
            CreateServiceDTO createDTO = new CreateServiceDTO(
                "Premium Service",
                new BigDecimal("299.99"),  // Precise decimal
                120,
                "High-end service"
            );

            Service savedService = new Service();
            savedService.setId(1L);
            savedService.setPrice(createDTO.price());

            when(serviceRepository.save(any(Service.class)))
                .thenReturn(savedService);
            
            // ACT
            ServiceDTO result = serviceService.createService(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            // BigDecimal comparison should be exact
            verify(serviceRepository, times(1)).save(argThat(service -> 
                service.getPrice().compareTo(new BigDecimal("299.99")) == 0
            ));
        }

        @Test
        @DisplayName("Should create service with minimum valid duration")
        void shouldCreateService_withMinimumDuration() {
            // ARRANGE
            CreateServiceDTO createDTO = new CreateServiceDTO(
                "Quick Service",
                new BigDecimal("50.00"),
                15,  // Minimum duration
                "Fast service"
            );

            Service savedService = new Service();
            savedService.setId(1L);
            savedService.setDurationMinutes(15);

            when(serviceRepository.save(any(Service.class)))
                .thenReturn(savedService);
            
            // ACT
            ServiceDTO result = serviceService.createService(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(serviceRepository, times(1)).save(any(Service.class));
        }
    }
}
