package com.aroundvan.backend.gas.dto;

import com.aroundvan.backend.gas.FuelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record GasImportRequest(
        @NotBlank
        @Size(min = 3, max = 10)
        String postalCodePrefix,

        @NotNull
        FuelType fuelType,

        @NotEmpty
        List<@Valid Station> stations
) {

    public record Station(
            @NotBlank String name,
            @NotBlank String address,
            @NotNull BigDecimal price,
            Double latitude,
            Double longitude
    ) {
    }
}
