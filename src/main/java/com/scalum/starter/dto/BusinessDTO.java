package com.scalum.starter.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class BusinessDTO {
    private UUID id;
    private UUID parentId;
    
    @NotBlank
    private String businessName;
    
    private String taxId;
    private UUID createdByUserId;
    private String treePath;
    private String settingsSnapshot;
    private boolean isActive;
}
