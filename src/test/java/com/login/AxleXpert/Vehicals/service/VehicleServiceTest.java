package com.login.AxleXpert.Vehicals.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.Vehicals.dto.VehicleDTO;
import com.login.AxleXpert.Vehicals.entity.Vehicle;
import com.login.AxleXpert.Vehicals.repository.VehicleRepository;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for VehicleService
 * 
 * Learning Focus:
 * - Testing user-vehicle relationships
 * - Testing filtering by user (getVehiclesByUserId)
 * - Testing Optional user assignment
 * - Testing entity mapping with multiple properties
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    private Vehicle testVehicle;
    private User testUser;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository, userRepository);
        testUser = TestDataBuilder.createUser();
        testVehicle = TestDataBuilder.createVehicle();
        testVehicle.setUser(testUser);
    }

    @Nested
    @DisplayName("Get Vehicle Tests")
    class GetVehicleTests {

        @Test
        @DisplayName("Should return all vehicles")
        void shouldReturnAllVehicles() {
            // ARRANGE
            Vehicle vehicle2 = TestDataBuilder.createVehicle();
            vehicle2.setId(2L);
            vehicle2.setMake("Honda");

            when(vehicleRepository.findAll())
                .thenReturn(Arrays.asList(testVehicle, vehicle2));
            
            // ACT
            List<VehicleDTO> result = vehicleService.getAllVehicles();
            
            // ASSERT
            assertThat(result).hasSize(2);
            verify(vehicleRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no vehicles exist")
        void shouldReturnEmptyList_whenNoVehicles() {
            // ARRANGE
            when(vehicleRepository.findAll())
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<VehicleDTO> result = vehicleService.getAllVehicles();
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return vehicle by ID when exists")
        void shouldReturnVehicle_whenExists() {
            // ARRANGE
            when(vehicleRepository.findById(testVehicle.getId()))
                .thenReturn(Optional.of(testVehicle));
            
            // ACT
            Optional<VehicleDTO> result = vehicleService.getVehicleById(testVehicle.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result.get().getMake()).isEqualTo(testVehicle.getMake());
            verify(vehicleRepository, times(1)).findById(testVehicle.getId());
        }

        @Test
        @DisplayName("Should return empty when vehicle not found")
        void shouldReturnEmpty_whenVehicleNotFound() {
            // ARRANGE
            when(vehicleRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<VehicleDTO> result = vehicleService.getVehicleById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return vehicles by user ID")
        void shouldReturnVehicles_byUserId() {
            // ARRANGE
            Vehicle vehicle2 = TestDataBuilder.createVehicle();
            vehicle2.setId(2L);
            vehicle2.setUser(testUser);

            when(vehicleRepository.findByUser_Id(testUser.getId()))
                .thenReturn(Arrays.asList(testVehicle, vehicle2));
            
            // ACT
            List<VehicleDTO> result = vehicleService.getVehiclesByUserId(testUser.getId());
            
            // ASSERT
            assertThat(result).hasSize(2);
            verify(vehicleRepository, times(1)).findByUser_Id(testUser.getId());
        }

        @Test
        @DisplayName("Should return empty list when user has no vehicles")
        void shouldReturnEmptyList_whenUserHasNoVehicles() {
            // ARRANGE
            when(vehicleRepository.findByUser_Id(anyLong()))
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<VehicleDTO> result = vehicleService.getVehiclesByUserId(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Create Vehicle Tests")
    class CreateVehicleTests {

        @Test
        @DisplayName("Should create vehicle successfully with user")
        void shouldCreateVehicle_withUser() {
            // ARRANGE
            VehicleDTO createDTO = new VehicleDTO();
            createDTO.setType("Sedan");
            createDTO.setYear(2020);
            createDTO.setMake("Toyota");
            createDTO.setModel("Camry");
            createDTO.setFuelType("Petrol");
            createDTO.setPlateNumber("ABC-123");
            createDTO.setUserId(testUser.getId());

            when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
            when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(testVehicle);
            
            // ACT
            VehicleDTO result = vehicleService.createVehicle(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(userRepository, times(1)).findById(testUser.getId());
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should create vehicle without user")
        void shouldCreateVehicle_withoutUser() {
            // ARRANGE
            VehicleDTO createDTO = new VehicleDTO();
            createDTO.setType("Sedan");
            createDTO.setYear(2020);
            createDTO.setMake("Toyota");
            createDTO.setModel("Camry");
            createDTO.setUserId(null);  // No user

            when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(testVehicle);
            
            // ACT
            VehicleDTO result = vehicleService.createVehicle(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(userRepository, never()).findById(anyLong());
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should not throw when user not found (Optional behavior)")
        void shouldNotThrow_whenUserNotFound() {
            // ARRANGE
            VehicleDTO createDTO = new VehicleDTO();
            createDTO.setType("Sedan");
            createDTO.setYear(2020);
            createDTO.setMake("Toyota");
            createDTO.setModel("Camry");
            createDTO.setUserId(999L);

            when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(testVehicle);
            
            // ACT & ASSERT
            assertThatCode(() -> vehicleService.createVehicle(createDTO))
                .doesNotThrowAnyException();
            
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }
    }

    @Nested
    @DisplayName("Update Vehicle Tests")
    class UpdateVehicleTests {

        @Test
        @DisplayName("Should update vehicle successfully")
        void shouldUpdateVehicle_successfully() {
            // ARRANGE
            VehicleDTO updateDTO = new VehicleDTO();
            updateDTO.setType("SUV");
            updateDTO.setYear(2023);
            updateDTO.setMake("Honda");
            updateDTO.setModel("CR-V");
            updateDTO.setUserId(testUser.getId());

            when(vehicleRepository.findById(testVehicle.getId()))
                .thenReturn(Optional.of(testVehicle));
            when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
            when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(testVehicle);
            
            // ACT
            Optional<VehicleDTO> result = vehicleService.updateVehicle(testVehicle.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(vehicleRepository, times(1)).save(testVehicle);
        }

        @Test
        @DisplayName("Should return empty when updating non-existent vehicle")
        void shouldReturnEmpty_whenVehicleNotFound() {
            // ARRANGE
            VehicleDTO updateDTO = new VehicleDTO();
            updateDTO.setMake("Toyota");

            when(vehicleRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<VehicleDTO> result = vehicleService.updateVehicle(999L, updateDTO);
            
            // ASSERT
            assertThat(result).isEmpty();
            verify(vehicleRepository, never()).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should update vehicle without changing user")
        void shouldUpdateVehicle_withoutUser() {
            // ARRANGE
            VehicleDTO updateDTO = new VehicleDTO();
            updateDTO.setType("Sedan");
            updateDTO.setYear(2024);
            updateDTO.setUserId(null);  // No user update

            when(vehicleRepository.findById(testVehicle.getId()))
                .thenReturn(Optional.of(testVehicle));
            when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(testVehicle);
            
            // ACT
            Optional<VehicleDTO> result = vehicleService.updateVehicle(testVehicle.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(userRepository, never()).findById(anyLong());
            verify(vehicleRepository, times(1)).save(testVehicle);
        }
    }

    @Nested
    @DisplayName("Delete Vehicle Tests")
    class DeleteVehicleTests {

        @Test
        @DisplayName("Should delete vehicle successfully")
        void shouldDeleteVehicle_successfully() {
            // ARRANGE
            when(vehicleRepository.existsById(testVehicle.getId()))
                .thenReturn(true);
            doNothing().when(vehicleRepository).deleteById(testVehicle.getId());
            
            // ACT
            boolean result = vehicleService.deleteVehicle(testVehicle.getId());
            
            // ASSERT
            assertThat(result).isTrue();
            verify(vehicleRepository, times(1)).deleteById(testVehicle.getId());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent vehicle")
        void shouldReturnFalse_whenVehicleNotFound() {
            // ARRANGE
            when(vehicleRepository.existsById(anyLong()))
                .thenReturn(false);
            
            // ACT
            boolean result = vehicleService.deleteVehicle(999L);
            
            // ASSERT
            assertThat(result).isFalse();
            verify(vehicleRepository, never()).deleteById(anyLong());
        }
    }
}
