package com.scalum.starter.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = false, exclude = "properties")
@ToString(exclude = "properties")
@Table(name = "business_channel")
public class BusinessChannel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false)
    private String value;

    private String label;

    private boolean isPrimary;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isConnected = false;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessChannelProperty> properties = new ArrayList<>();
}
