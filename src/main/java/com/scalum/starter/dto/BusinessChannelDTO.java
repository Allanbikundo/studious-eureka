package com.scalum.starter.dto;

import com.scalum.starter.model.ChannelPropertyKey;
import com.scalum.starter.model.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class BusinessChannelDTO {
    private Long id;
    private UUID businessId;

    @NotNull private ChannelType type;

    @NotBlank private String value;

    private String label;
    private boolean isPrimary;
    private boolean isConnected;
    private boolean isActive;

    private Map<ChannelPropertyKey, String> properties;
}
