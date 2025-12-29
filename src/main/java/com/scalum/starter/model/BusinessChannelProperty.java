package com.scalum.starter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(
        name = "business_channel_property",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"channel_id", "property_key"})})
@EqualsAndHashCode(exclude = "channel")
@ToString(exclude = "channel")
public class BusinessChannelProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private BusinessChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_key", nullable = false)
    private ChannelPropertyKey key;

    @Column(nullable = false)
    private String value;
}
