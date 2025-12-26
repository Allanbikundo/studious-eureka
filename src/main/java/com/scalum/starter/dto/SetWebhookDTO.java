package com.scalum.starter.dto;

import com.scalum.starter.domain.evolution.EvolutionWebhookEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SetWebhookDTO {
    @NotBlank private String webhookUrl;

    @NotEmpty private List<EvolutionWebhookEvent> events;
}
