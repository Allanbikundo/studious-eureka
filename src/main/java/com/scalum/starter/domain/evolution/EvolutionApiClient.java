package com.scalum.starter.domain.evolution;

import com.scalum.starter.domain.evolution.dto.*;
import com.scalum.starter.model.BusinessContact;
import com.scalum.starter.model.BusinessContactProperty;
import com.scalum.starter.model.ContactPropertyKey;
import java.io.IOException;
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

    public void sendMessage(BusinessContact contact, String number, String text) {
        Map<ContactPropertyKey, String> properties = getPropertiesMap(contact);

        String instanceId = properties.get(ContactPropertyKey.INSTANCE_ID);
        String token = properties.get(ContactPropertyKey.API_TOKEN);

        String apiKeyToUse = token != null ? token : globalApiKey;

        if (instanceId == null) {
            log.error("Missing instance ID for contact ID: {}", contact.getId());
            throw new IllegalArgumentException("Missing Evolution API instance ID");
        }

        EvolutionTextRequest request =
                EvolutionTextRequest.builder()
                        .number(number)
                        .options(
                                EvolutionOptions.builder()
                                        .delay(1200)
                                        .presence("composing")
                                        .linkPreview(false)
                                        .build())
                        .textMessage(EvolutionTextMessage.builder().text(text).build())
                        .build();

        try {
            Response<Object> response =
                    evolutionApi.sendText(instanceId, apiKeyToUse, request).execute();
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

    private Map<ContactPropertyKey, String> getPropertiesMap(BusinessContact contact) {
        return contact.getProperties().stream()
                .collect(
                        Collectors.toMap(
                                BusinessContactProperty::getKey,
                                BusinessContactProperty::getValue));
    }
}
