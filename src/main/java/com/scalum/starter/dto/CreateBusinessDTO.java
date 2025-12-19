package com.scalum.starter.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateBusinessDTO {
    private UUID parentId;
    
    @NotBlank
    private String businessName;
    
    private String taxId;
}
