package com.login.AxleXpert.Branches;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String mapLink;
    private String openHours;
    private String closeHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long managerId;
    private String managerName;

    // Convenience constructor to create a DTO from the entity
    public BranchDTO(Branch branch) {
        if (branch == null) return;
        this.id = branch.getId();
        this.managerId = branch.getManager() != null ? branch.getManager().getId() : null;
        this.managerName = branch.getManager() != null ? branch.getManager().getUsername() : null;
        this.name = branch.getName();
        this.address = branch.getAddress();
        this.phone = branch.getPhone();
        this.email = branch.getEmail();
        this.mapLink = branch.getMapLink();
        this.openHours = branch.getOpenHours();
        this.closeHours = branch.getCloseHours();
        this.createdAt = branch.getCreatedAt();
        this.updatedAt = branch.getUpdatedAt();
    }
}

