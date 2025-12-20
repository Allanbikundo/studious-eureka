package com.scalum.starter.domain.evolution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateInstanceRequest {
    private String instanceName;
    private String integration;
    private String token;
}
