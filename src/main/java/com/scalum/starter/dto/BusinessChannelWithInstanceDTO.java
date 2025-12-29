package com.scalum.starter.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessChannelWithInstanceDTO extends BusinessChannelDTO {
    private String instanceName;
    private String instanceStatus;
}
