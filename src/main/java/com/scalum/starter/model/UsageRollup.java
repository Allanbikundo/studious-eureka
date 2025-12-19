package com.scalum.starter.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "usage_rollup")
public class UsageRollup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID rootBusinessId;

    @Column(nullable = false)
    private LocalDate period;

    @Column(nullable = false)
    private long totalConversations;

    @Column(nullable = false)
    private long totalApiCalls;

    @Column(nullable = false)
    private BigDecimal billedAmount;
}
