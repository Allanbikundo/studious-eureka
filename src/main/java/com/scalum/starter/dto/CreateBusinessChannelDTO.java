package com.scalum.starter.dto;

import com.scalum.starter.model.ChannelPropertyKey;
import com.scalum.starter.model.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;

@Data
public class CreateBusinessChannelDTO {
    @NotNull private ChannelType type;

    @NotBlank private String value;

    private String label;
    private boolean isPrimary;

    private Map<ChannelPropertyKey, String> properties;
}
