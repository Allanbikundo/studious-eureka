package com.scalum.starter.domain.evolution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvolutionWebhookResponse {
    @JsonProperty("event")
    private String event;

    @JsonProperty("instance")
    private String instance;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("server_url")
    private String serverUrl;

    @JsonProperty("apikey")
    private String apikey;
}

@lombok.Data
class Message {
    @JsonProperty("conversation")
    private String conversation;

    @JsonProperty("messageContextInfo")
    private MessageContextInfo messageContextInfo;
}

class MessageContextInfo {
    @JsonProperty("threadId")
    private List<Object> threadId;

    @JsonProperty("deviceListMetadataVersion")
    private Integer deviceListMetadataVersion;
}
