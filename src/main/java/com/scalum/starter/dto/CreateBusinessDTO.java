package com.scalum.starter.dto;

import com.scalum.starter.model.BusinessSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateBusinessDTO {
    private UUID parentId;
    
    @NotBlank
    private String businessName;
    
    private String taxId;

    @NotNull
    private Long industryId;

    @NotNull
    private BusinessSize businessSize;

    private String website;

    private String location;
}
