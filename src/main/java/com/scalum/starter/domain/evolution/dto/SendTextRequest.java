package com.scalum.starter.domain.evolution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendTextRequest {
    private String number;
    private String text;
}
