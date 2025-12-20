package com.scalum.starter.domain.evolution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvolutionOptions {
    private int delay;
    private String presence;
    private boolean linkPreview;
}
