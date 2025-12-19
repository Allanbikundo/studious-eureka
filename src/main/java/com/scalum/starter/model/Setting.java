package com.scalum.starter.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String key;

    private String description;

    private String defaultValue;
}
