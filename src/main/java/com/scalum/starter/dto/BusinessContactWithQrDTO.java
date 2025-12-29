package com.scalum.starter.dto;

import com.scalum.starter.domain.evolution.dto.CreateInstanceResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessContactWithQrDTO extends BusinessChannelDTO {
    private CreateInstanceResponse.Qrcode qrcode;
    private String instanceName;
}
