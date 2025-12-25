package com.scalum.starter.dto;

import com.scalum.starter.model.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInstanceAndContactDTO {
    @NotNull private ContactType type;

    @NotBlank private String value; // Phone number, email, or URL

    private String label;
    private boolean isPrimary;
}
