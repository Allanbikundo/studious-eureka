package com.scalum.starter.dto;

import com.scalum.starter.model.BusinessSize;
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
    
    private Long industryId;
    private BusinessSize businessSize;
    private String website;
    private String location;
}
