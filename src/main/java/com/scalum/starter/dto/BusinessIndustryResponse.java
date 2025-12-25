package com.scalum.starter.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class BusinessIndustryResponse {
    private UUID id;
    private String name;
    private String description;
}
