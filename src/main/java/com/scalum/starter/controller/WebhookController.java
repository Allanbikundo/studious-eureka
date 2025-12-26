package com.scalum.starter.controller;

import com.scalum.starter.domain.evolution.dto.EvolutionWebhookResponse;
import com.scalum.starter.domain.webhook.WebhookService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/evolution/{instanceId}")
    public ResponseEntity<Void> handleEvolutionWebhook(
            @PathVariable String instanceId, @RequestBody EvolutionWebhookResponse payload) {
        log.info("Received Evolution API webhook for instance: {}", instanceId);
        webhookService.processEvolutionWebhook(instanceId, payload);
        return ResponseEntity.ok().build();
    }
}
