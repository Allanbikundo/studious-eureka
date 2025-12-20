package com.scalum.starter.domain.evolution.dto;

import lombok.Data;

@Data
public class ConnectInstanceResponse {
    private String pairingCode;
    private String code;
    private String base64;
    private int count;
}
