package com.scalum.starter.domain.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalum.starter.domain.evolution.EvolutionApiClient;
import com.scalum.starter.domain.evolution.dto.EvolutionWebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final ObjectMapper objectMapper;

    private final EvolutionApiClient evolutionApiClient;

    public void processEvolutionWebhook(String instanceId, EvolutionWebhookResponse payload) {
        try {

            String jsonPayload =
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            log.info("Received webhook for instance {}: \n{}", instanceId, jsonPayload);

            // check if the event is an Upsert and its not from me
            if (payload.getEvent().equals("messages.upsert")
                    && !payload.getData().getKey().getFromMe()) {
                String phoneNumber = payload.getData().getKey().getRemoteJid().split("@")[0];
                String name = payload.getData().getPushName();
                //                evolutionApiClient.sendMessage(phoneNumber, "Hello there",
                // payload.getInstance());
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing webhook payload for instance {}", instanceId, e);
        }
    }
}
