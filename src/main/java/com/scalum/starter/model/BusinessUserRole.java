package com.scalum.starter.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "business_user_role", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "business_id"})
})
public class BusinessUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
