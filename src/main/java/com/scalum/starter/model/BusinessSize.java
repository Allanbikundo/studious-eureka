package com.scalum.starter.model;

public enum BusinessSize {
    MICRO("Micro Enterprise (Fewer than 10 employees)"),
    SMALL("Small Enterprise (10 to 49 employees)"),
    MEDIUM("Medium Enterprise (50 to 249 employees)"),
    LARGE("Large Enterprise (250 or more employees)");

    private final String description;

    BusinessSize(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
