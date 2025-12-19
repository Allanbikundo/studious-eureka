package com.scalum.starter.dto;

import com.scalum.starter.model.ContactPropertyKey;
import com.scalum.starter.model.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class BusinessContactDTO {
    private Long id;
    private UUID businessId;
    
    @NotNull
    private ContactType type;
    
    @NotBlank
    private String value;
    
    private String label;
    private boolean isPrimary;
    
    private Map<ContactPropertyKey, String> properties;
}
