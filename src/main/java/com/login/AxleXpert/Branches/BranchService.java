package com.login.AxleXpert.Branches;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Users.User;
import com.login.AxleXpert.Users.UserRepository;

@Service
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    public BranchService(BranchRepository branchRepository, UserRepository userRepository) {
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(BranchDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BranchDTO> getBranchById(Long id) {
        return branchRepository.findById(id)
                .map(BranchDTO::new);
    }

    public BranchDTO createBranch(BranchDTO branchDTO) {
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setAddress(branchDTO.getAddress());
        branch.setPhone(branchDTO.getPhone());
        branch.setEmail(branchDTO.getEmail());
        branch.setMapLink(branchDTO.getMapLink());
        branch.setOpenHours(branchDTO.getOpenHours());
        branch.setCloseHours(branchDTO.getCloseHours());
        
        // Set manager if managerId is provided
        if (branchDTO.getManagerId() != null) {
            User manager = userRepository.findById(branchDTO.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + branchDTO.getManagerId()));
            branch.setManager(manager);
        }
        
        Branch savedBranch = branchRepository.save(branch);
        return new BranchDTO(savedBranch);
    }

    public Optional<BranchDTO> updateBranch(Long id, BranchDTO branchDTO) {
        return branchRepository.findById(id)
                .map(branch -> {
                    branch.setName(branchDTO.getName());
                    branch.setAddress(branchDTO.getAddress());
                    branch.setPhone(branchDTO.getPhone());
                    branch.setEmail(branchDTO.getEmail());
                    branch.setMapLink(branchDTO.getMapLink());
                    branch.setOpenHours(branchDTO.getOpenHours());
                    branch.setCloseHours(branchDTO.getCloseHours());
                    
                    // Update manager if managerId is provided
                    if (branchDTO.getManagerId() != null) {
                        User manager = userRepository.findById(branchDTO.getManagerId())
                                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + branchDTO.getManagerId()));
                        branch.setManager(manager);
                    } else {
                        // If managerId is null, remove the manager assignment
                        branch.setManager(null);
                    }
                    
                    Branch updatedBranch = branchRepository.save(branch);
                    return new BranchDTO(updatedBranch);
                });
    }

    public boolean deleteBranch(Long id) {
        if (branchRepository.existsById(id)) {
            branchRepository.deleteById(id);
            return true;
        }
        return false;
    }
}