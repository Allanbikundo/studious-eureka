package com.scalum.starter.domain.evolution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Data {
    @JsonProperty("key")
    private Key key;

    @JsonProperty("pushName")
    private String pushName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private Message message;

    @JsonProperty("messageType")
    private String messageType;

    @JsonProperty("messageTimestamp")
    private Integer messageTimestamp;

    @JsonProperty("instanceId")
    private String instanceId;

    @JsonProperty("source")
    private String source;
}
