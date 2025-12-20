package com.scalum.starter.domain.evolution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateInstanceResponse {
    private Instance instance;
    private String hash;
    private Qrcode qrcode; // Present in connect, but might be null in create
    private Webhook webhook;
    private Websocket websocket;
    private Rabbitmq rabbitmq;
    private Sqs sqs;
    private Settings settings;

    @Data
    public static class Instance {
        private String instanceName;
        private String instanceId;
        private String integration;
        private String webhookWaBusiness;
        private String accessTokenWaBusiness;
        private String status;
    }

    @Data
    public static class Qrcode {
        private String code;
        private String base64;
    }

    @Data
    public static class Webhook {
        // Assuming empty or with fields to be added later
    }

    @Data
    public static class Websocket {
        // Assuming empty or with fields to be added later
    }

    @Data
    public static class Rabbitmq {
        // Assuming empty or with fields to be added later
    }

    @Data
    public static class Sqs {
        // Assuming empty or with fields to be added later
    }

    @Data
    public static class Settings {
        private boolean rejectCall;
        private String msgCall;
        private boolean groupsIgnore;
        private boolean alwaysOnline;
        private boolean readMessages;
        private boolean readStatus;
        private boolean syncFullHistory;
        private String wavoipToken;
    }
}
