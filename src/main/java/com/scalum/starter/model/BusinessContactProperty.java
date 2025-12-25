package com.scalum.starter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(
        name = "business_contact_property",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"contact_id", "property_key"})})
@EqualsAndHashCode(exclude = "contact")
@ToString(exclude = "contact")
public class BusinessContactProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private BusinessContact contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_key", nullable = false)
    private ContactPropertyKey key;

    @Column(nullable = false)
    private String value;
}
