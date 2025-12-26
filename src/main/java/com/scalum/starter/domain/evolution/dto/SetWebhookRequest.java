package com.scalum.starter.domain.evolution.dto;

import com.scalum.starter.domain.evolution.EvolutionWebhookEvent;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetWebhookRequest {
    private Webhook webhook;

    @Data
    @Builder
    public static class Webhook {
        private boolean enabled;
        private String url;
        private List<EvolutionWebhookEvent> events;
        private boolean base64;
        private boolean byEvents;
    }
}
