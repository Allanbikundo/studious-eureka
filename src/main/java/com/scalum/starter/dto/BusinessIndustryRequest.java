package com.scalum.starter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessIndustryRequest {
    @NotBlank(message = "Industry name is required")
    private String name;

    private String description;
}
