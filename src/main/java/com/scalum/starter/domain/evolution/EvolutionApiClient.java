package com.scalum.starter.domain.evolution;

import com.scalum.starter.domain.evolution.dto.*;
import com.scalum.starter.model.BusinessChannel;
import com.scalum.starter.model.BusinessChannelProperty;
import com.scalum.starter.model.ChannelPropertyKey;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Response;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvolutionApiClient {

    private final EvolutionApi evolutionApi;

    @Value("${evolution.api-key}")
    private String globalApiKey;

    public void sendMessage(String number, String text, String instanceId) {
        SendTextRequest request = SendTextRequest.builder().number(number).text(text).build();

        try {
            Response<Object> response =
                    evolutionApi.sendSimpleText(instanceId, globalApiKey, request).execute();
            if (!response.isSuccessful()) {
                String errorBody =
                        response.errorBody() != null ? response.errorBody().string() : "null";
                log.error(
                        "Failed to send message via Evolution API. Code: {}, Body: {}",
                        response.code(),
                        errorBody);
                throw new RuntimeException("Evolution API call failed: " + response.code());
            }
            log.info("Message sent successfully to {}", number);
        } catch (IOException e) {
            log.error("Error sending message via Evolution API", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public CreateInstanceResponse createInstance(String instanceName, String token) {
        CreateInstanceRequest request =
                CreateInstanceRequest.builder()
                        .instanceName(instanceName)
                        .integration("WHATSAPP-BAILEYS")
                        .token(token)
                        .build();

        try {
            Response<CreateInstanceResponse> response =
                    evolutionApi.createInstance(globalApiKey, request).execute();
            if (!response.isSuccessful()) {
                String errorBody =
                        response.errorBody() != null ? response.errorBody().string() : "null";
                log.error(
                        "Failed to create instance. Code: {}, Body: {}",
                        response.code(),
                        errorBody);
                throw new RuntimeException(
                        "Evolution API create instance failed: " + response.code());
            }
            CreateInstanceResponse body = response.body();
            if (body == null) {
                throw new RuntimeException("Evolution API create instance returned an empty body.");
            }
            return body;
        } catch (IOException e) {
            log.error("Error creating instance via Evolution API", e);
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    public Optional<ConnectInstanceResponse> connectInstance(String instanceName) {
        try {
            Response<ConnectInstanceResponse> response =
                    evolutionApi.connectInstance(instanceName, globalApiKey).execute();

            if (response.isSuccessful()) {
                return Optional.ofNullable(response.body());
            }

            if (response.code() == 404) {
                log.info("Instance '{}' not found when trying to connect.", instanceName);
                return Optional.empty();
            }

            String errorBody =
                    response.errorBody() != null ? response.errorBody().string() : "null";
            log.error(
                    "Failed to connect instance '{}'. Code: {}, Body: {}",
                    instanceName,
                    response.code(),
                    errorBody);
            throw new RuntimeException(
                    "Evolution API connect instance failed with code: " + response.code());

        } catch (IOException e) {
            log.error("Error connecting instance '{}' via Evolution API", instanceName, e);
            throw new RuntimeException("Failed to connect instance", e);
        }
    }

    public void setWebhook(
            String instanceName, String webhookUrl, List<EvolutionWebhookEvent> events) {
        SetWebhookRequest.Webhook webhook =
                SetWebhookRequest.Webhook.builder()
                        .enabled(true)
                        .url(webhookUrl)
                        .events(events)
                        .base64(false)
                        .byEvents(false)
                        .build();

        SetWebhookRequest request = SetWebhookRequest.builder().webhook(webhook).build();

        try {
            Response<Object> response =
                    evolutionApi.setWebhook(instanceName, globalApiKey, request).execute();
            if (!response.isSuccessful()) {
                String errorBody =
                        response.errorBody() != null ? response.errorBody().string() : "null";
                log.error(
                        "Failed to set webhook for instance '{}'. Code: {}, Body: {}",
                        instanceName,
                        response.code(),
                        errorBody);
                throw new RuntimeException("Evolution API set webhook failed: " + response.code());
            }
            log.info("Webhook set successfully for instance '{}'", instanceName);
        } catch (IOException e) {
            log.error("Error setting webhook for instance '{}' via Evolution API", instanceName, e);
            throw new RuntimeException("Failed to set webhook", e);
        }
    }

    private Map<ChannelPropertyKey, String> getPropertiesMap(BusinessChannel contact) {
        return contact.getProperties().stream()
                .collect(
                        Collectors.toMap(
                                BusinessChannelProperty::getKey,
                                BusinessChannelProperty::getValue));
    }
}
