package com.aroundvan.backend.environment.aqhi;

import lombok.Getter;

@Getter
public enum AqhiRiskLevel {

    LOW("Low", "Ideal air quality for outdoor activities."),
    MODERATE("Moderate", "Unless if you have symptoms, its safe outside for outdoor activities."),
    HIGH("High", "Consider reducing or rescheduling some outdoor activities."),
    VERY_HIGH("Very High", "Reduce or reschedule all outdoor activities.");

    private final String label;
    private final String healthMessage;

    AqhiRiskLevel(String label, String healthMessage) {
        this.label = label;
        this.healthMessage = healthMessage;
    }

    public static AqhiRiskLevel fromValue(double value) {
        if (value <= 3) return LOW;
        if (value <= 6) return MODERATE;
        if (value <= 10) return HIGH;
        return VERY_HIGH;
    }
}
