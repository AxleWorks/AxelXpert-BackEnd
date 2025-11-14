package com.login.AxleXpert.Branches.service;

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

import com.login.AxleXpert.Branches.dto.BranchDTO;
import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Branches.repository.BranchRepository;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for BranchService
 * 
 * Learning Focus:
 * - Testing service with multiple dependencies (BranchRepository, UserRepository)
 * - Testing manager assignment logic
 * - Testing Optional return types
 * - Testing entity relationships (Branch-Manager)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BranchService Unit Tests")
class BranchServiceTest {

    private BranchService branchService;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private UserRepository userRepository;

    private Branch testBranch;
    private User testManager;

    @BeforeEach
    void setUp() {
        branchService = new BranchService(branchRepository, userRepository);
        testManager = TestDataBuilder.createUser();
        testBranch = TestDataBuilder.createBranch();
        testBranch.setManager(testManager);
    }

    @Nested
    @DisplayName("Get Branch Tests")
    class GetBranchTests {

        @Test
        @DisplayName("Should return all branches")
        void shouldReturnAllBranches() {
            // ARRANGE
            Branch branch2 = TestDataBuilder.createBranch();
            branch2.setId(2L);
            branch2.setName("Downtown Branch");

            when(branchRepository.findAll())
                .thenReturn(Arrays.asList(testBranch, branch2));
            
            // ACT
            List<BranchDTO> result = branchService.getAllBranches();
            
            // ASSERT
            assertThat(result).hasSize(2);
            verify(branchRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no branches exist")
        void shouldReturnEmptyList_whenNoBranches() {
            // ARRANGE
            when(branchRepository.findAll())
                .thenReturn(Collections.emptyList());
            
            // ACT
            List<BranchDTO> result = branchService.getAllBranches();
            
            // ASSERT
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return branch by ID when exists")
        void shouldReturnBranch_whenExists() {
            // ARRANGE
            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            
            // ACT
            Optional<BranchDTO> result = branchService.getBranchById(testBranch.getId());
            
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(testBranch.getName());
            verify(branchRepository, times(1)).findById(testBranch.getId());
        }

        @Test
        @DisplayName("Should return empty when branch not found")
        void shouldReturnEmpty_whenBranchNotFound() {
            // ARRANGE
            when(branchRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<BranchDTO> result = branchService.getBranchById(999L);
            
            // ASSERT
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Create Branch Tests")
    class CreateBranchTests {

        @Test
        @DisplayName("Should create branch successfully with manager")
        void shouldCreateBranch_withManager() {
            // ARRANGE
            BranchDTO createDTO = new BranchDTO();
            createDTO.setName("New Branch");
            createDTO.setAddress("123 Main St");
            createDTO.setPhone("555-0123");
            createDTO.setEmail("branch@test.com");
            createDTO.setManagerId(testManager.getId());

            when(userRepository.findById(testManager.getId()))
                .thenReturn(Optional.of(testManager));
            when(branchRepository.save(any(Branch.class)))
                .thenReturn(testBranch);
            
            // ACT
            BranchDTO result = branchService.createBranch(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(userRepository, times(1)).findById(testManager.getId());
            verify(branchRepository, times(1)).save(any(Branch.class));
        }

        @Test
        @DisplayName("Should create branch without manager")
        void shouldCreateBranch_withoutManager() {
            // ARRANGE
            BranchDTO createDTO = new BranchDTO();
            createDTO.setName("New Branch");
            createDTO.setAddress("123 Main St");
            createDTO.setPhone("555-0123");
            createDTO.setEmail("branch@test.com");
            createDTO.setManagerId(null);  // No manager

            when(branchRepository.save(any(Branch.class)))
                .thenReturn(testBranch);
            
            // ACT
            BranchDTO result = branchService.createBranch(createDTO);
            
            // ASSERT
            assertThat(result).isNotNull();
            verify(userRepository, never()).findById(anyLong());
            verify(branchRepository, times(1)).save(any(Branch.class));
        }

        @Test
        @DisplayName("Should throw exception when manager not found")
        void shouldThrowException_whenManagerNotFound() {
            // ARRANGE
            BranchDTO createDTO = new BranchDTO();
            createDTO.setName("New Branch");
            createDTO.setManagerId(999L);

            when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> branchService.createBranch(createDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Manager not found");
            
            verify(branchRepository, never()).save(any(Branch.class));
        }
    }

    @Nested
    @DisplayName("Update Branch Tests")
    class UpdateBranchTests {

        @Test
        @DisplayName("Should update branch successfully")
        void shouldUpdateBranch_successfully() {
            // ARRANGE
            BranchDTO updateDTO = new BranchDTO();
            updateDTO.setName("Updated Branch Name");
            updateDTO.setAddress("456 Updated St");
            updateDTO.setPhone("555-9999");
            updateDTO.setEmail("updated@test.com");
            updateDTO.setManagerId(testManager.getId());

            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(userRepository.findById(testManager.getId()))
                .thenReturn(Optional.of(testManager));
            when(branchRepository.save(any(Branch.class)))
                .thenReturn(testBranch);
            
            // ACT
            Optional<BranchDTO> result = branchService.updateBranch(testBranch.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(branchRepository, times(1)).save(testBranch);
        }

        @Test
        @DisplayName("Should return empty when updating non-existent branch")
        void shouldReturnEmpty_whenBranchNotFound() {
            // ARRANGE
            BranchDTO updateDTO = new BranchDTO();
            updateDTO.setName("Updated Name");

            when(branchRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
            
            // ACT
            Optional<BranchDTO> result = branchService.updateBranch(999L, updateDTO);
            
            // ASSERT
            assertThat(result).isEmpty();
            verify(branchRepository, never()).save(any(Branch.class));
        }

        @Test
        @DisplayName("Should remove manager when managerId is null")
        void shouldRemoveManager_whenManagerIdNull() {
            // ARRANGE
            BranchDTO updateDTO = new BranchDTO();
            updateDTO.setName("Branch Name");
            updateDTO.setManagerId(null);  // Remove manager

            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(branchRepository.save(any(Branch.class)))
                .thenReturn(testBranch);
            
            // ACT
            Optional<BranchDTO> result = branchService.updateBranch(testBranch.getId(), updateDTO);
            
            // ASSERT
            assertThat(result).isPresent();
            verify(userRepository, never()).findById(anyLong());
            verify(branchRepository, times(1)).save(testBranch);
        }

        @Test
        @DisplayName("Should throw exception when new manager not found")
        void shouldThrowException_whenNewManagerNotFound() {
            // ARRANGE
            BranchDTO updateDTO = new BranchDTO();
            updateDTO.setName("Branch Name");
            updateDTO.setManagerId(999L);

            when(branchRepository.findById(testBranch.getId()))
                .thenReturn(Optional.of(testBranch));
            when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
            
            // ACT & ASSERT
            assertThatThrownBy(() -> branchService.updateBranch(testBranch.getId(), updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Manager not found");
            
            verify(branchRepository, never()).save(any(Branch.class));
        }
    }

    @Nested
    @DisplayName("Delete Branch Tests")
    class DeleteBranchTests {

        @Test
        @DisplayName("Should delete branch successfully")
        void shouldDeleteBranch_successfully() {
            // ARRANGE
            when(branchRepository.existsById(testBranch.getId()))
                .thenReturn(true);
            doNothing().when(branchRepository).deleteById(testBranch.getId());
            
            // ACT
            boolean result = branchService.deleteBranch(testBranch.getId());
            
            // ASSERT
            assertThat(result).isTrue();
            verify(branchRepository, times(1)).deleteById(testBranch.getId());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent branch")
        void shouldReturnFalse_whenBranchNotFound() {
            // ARRANGE
            when(branchRepository.existsById(anyLong()))
                .thenReturn(false);
            
            // ACT
            boolean result = branchService.deleteBranch(999L);
            
            // ASSERT
            assertThat(result).isFalse();
            verify(branchRepository, never()).deleteById(anyLong());
        }
    }
}
