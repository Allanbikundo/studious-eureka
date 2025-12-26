package com.scalum.starter.domain.evolution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Key {
    @JsonProperty("remoteJid")
    private String remoteJid;

    @JsonProperty("remoteJidAlt")
    private String remoteJidAlt;

    @JsonProperty("fromMe")
    private Boolean fromMe;

    @JsonProperty("id")
    private String id;

    @JsonProperty("participant")
    private String participant;

    @JsonProperty("addressingMode")
    private String addressingMode;
}
