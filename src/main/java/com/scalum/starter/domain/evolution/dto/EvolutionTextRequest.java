package com.scalum.starter.domain.evolution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvolutionTextRequest {
    private String number;
    private EvolutionOptions options;
    private EvolutionTextMessage textMessage;
}
