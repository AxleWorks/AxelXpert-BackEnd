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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Convenience constructor to create a DTO from the entity
    public BranchDTO(Branch branch) {
        if (branch == null) return;
        this.id = branch.getId();
        this.name = branch.getName();
        this.address = branch.getAddress();
        this.phone = branch.getPhone();
        this.createdAt = branch.getCreatedAt();
        this.updatedAt = branch.getUpdatedAt();
    }

}

