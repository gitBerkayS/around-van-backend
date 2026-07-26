package com.aroundvan.backend.environment.wildfire.fireweather;

import lombok.Getter;

@Getter
public enum FireDangerClass {

    VERY_LOW(1, "Very Low"),
    LOW(2, "Low"),
    MODERATE(3, "Moderate"),
    HIGH(4, "High"),
    EXTREME(5, "Extreme");

    private final int rating;
    private final String label;

    FireDangerClass(int rating, String label) {
        this.rating = rating;
        this.label = label;
    }

    public static FireDangerClass fromRating(Integer rating) {
        if (rating == null) {
            return null;
        }

        for (FireDangerClass dangerClass : values()) {
            if (dangerClass.rating == rating) {
                return dangerClass;
            }
        }

        return null;
    }
}
