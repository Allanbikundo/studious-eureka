package com.scalum.starter.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "business_setting", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"business_id", "setting_id"})
})
public class BusinessSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_id", nullable = false)
    private Setting setting;

    @Column(nullable = false)
    private String value;
}
