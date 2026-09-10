package com.aroundvan.backend.gas.dto;

import com.aroundvan.backend.gas.FuelType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GasTrendResponse(
        FuelType fuelType,
        LocalDate todayDate,
        BigDecimal todayAverage,
        BigDecimal comparisonAverage,
        ComparisonBasis comparisonBasis,
        LocalDate comparisonPeriodStart,
        LocalDate comparisonPeriodEnd,
        TrendDirection direction,
        BigDecimal delta
) {
    public enum ComparisonBasis {
        PREVIOUS_MONTH,
        CURRENT_MONTH_TO_DATE
    }

    public enum TrendDirection {
        UP,
        DOWN,
        FLAT,
        INSUFFICIENT_DATA
    }
}
