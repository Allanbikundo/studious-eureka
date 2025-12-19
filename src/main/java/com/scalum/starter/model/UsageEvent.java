package com.scalum.starter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "usage_event", indexes = {
    @Index(name = "idx_usage_event_business_timestamp", columnList = "businessId, timestamp")
})
public class UsageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID businessId;

    private String conversationId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private int apiCallsCount;
}
