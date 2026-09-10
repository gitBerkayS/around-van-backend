package com.aroundvan.backend.gas;

import com.aroundvan.backend.gas.dto.GasTrendResponse;
import com.aroundvan.backend.gas.dto.GasTrendResponse.ComparisonBasis;
import com.aroundvan.backend.gas.dto.GasTrendResponse.TrendDirection;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class GasTrendService {

    static final ZoneId VANCOUVER_ZONE = ZoneId.of("America/Vancouver");

    private final GasPriceDailyRepository gasPriceDailyRepository;

    @Transactional(readOnly = true)
    public GasTrendResponse getTrend(User user, FuelType fuelType) {
        FuelType resolvedFuel = resolveFuelType(user, fuelType);
        LocalDate today = LocalDate.now(VANCOUVER_ZONE);

        BigDecimal todayAverage = toScaledPrice(
                gasPriceDailyRepository.averagePriceForDate(resolvedFuel, today)
        );

        if (todayAverage == null) {
            return insufficient(resolvedFuel, today);
        }

        LocalDate previousMonthStart = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate previousMonthEnd = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        boolean hasPreviousMonth = gasPriceDailyRepository.existsByFuelTypeAndPriceDateBetween(
                resolvedFuel,
                previousMonthStart,
                previousMonthEnd
        );

        if (hasPreviousMonth) {
            BigDecimal comparisonAverage = toScaledPrice(
                    gasPriceDailyRepository.averageOfDailyAverages(
                            resolvedFuel.name(),
                            previousMonthStart,
                            previousMonthEnd
                    )
            );

            if (comparisonAverage == null) {
                return insufficient(resolvedFuel, today);
            }

            return buildResponse(
                    resolvedFuel,
                    today,
                    todayAverage,
                    comparisonAverage,
                    ComparisonBasis.PREVIOUS_MONTH,
                    previousMonthStart,
                    previousMonthEnd
            );
        }

        LocalDate currentMonthStart = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate yesterday = today.minusDays(1);

        if (yesterday.isBefore(currentMonthStart)) {
            return insufficient(resolvedFuel, today);
        }

        boolean hasPastDaysThisMonth = gasPriceDailyRepository.existsByFuelTypeAndPriceDateBetween(
                resolvedFuel,
                currentMonthStart,
                yesterday
        );

        if (!hasPastDaysThisMonth) {
            return insufficient(resolvedFuel, today);
        }

        BigDecimal comparisonAverage = toScaledPrice(
                gasPriceDailyRepository.averageOfDailyAverages(
                        resolvedFuel.name(),
                        currentMonthStart,
                        yesterday
                )
        );

        if (comparisonAverage == null) {
            return insufficient(resolvedFuel, today);
        }

        return buildResponse(
                resolvedFuel,
                today,
                todayAverage,
                comparisonAverage,
                ComparisonBasis.CURRENT_MONTH_TO_DATE,
                currentMonthStart,
                yesterday
        );
    }

    private GasTrendResponse buildResponse(
            FuelType fuelType,
            LocalDate today,
            BigDecimal todayAverage,
            BigDecimal comparisonAverage,
            ComparisonBasis basis,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        BigDecimal delta = todayAverage.subtract(comparisonAverage).setScale(3, RoundingMode.HALF_UP);
        TrendDirection direction = directionFrom(delta);

        return new GasTrendResponse(
                fuelType,
                today,
                todayAverage,
                comparisonAverage,
                basis,
                periodStart,
                periodEnd,
                direction,
                delta
        );
    }

    private GasTrendResponse insufficient(FuelType fuelType, LocalDate today) {
        return new GasTrendResponse(
                fuelType,
                today,
                null,
                null,
                null,
                null,
                null,
                TrendDirection.INSUFFICIENT_DATA,
                null
        );
    }

    private TrendDirection directionFrom(BigDecimal delta) {
        int compared = delta.compareTo(BigDecimal.ZERO);
        if (compared > 0) {
            return TrendDirection.UP;
        }
        if (compared < 0) {
            return TrendDirection.DOWN;
        }
        return TrendDirection.FLAT;
    }

    private BigDecimal toScaledPrice(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
    }

    private FuelType resolveFuelType(User user, FuelType override) {
        if (override != null) {
            return override;
        }

        return user.getPreferredFuelType() != null
                ? user.getPreferredFuelType()
                : FuelType.REGULAR;
    }
}
