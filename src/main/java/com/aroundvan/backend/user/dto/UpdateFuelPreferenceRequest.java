package com.aroundvan.backend.user.dto;

import com.aroundvan.backend.gas.FuelType;
import jakarta.validation.constraints.NotNull;

public record UpdateFuelPreferenceRequest(
        @NotNull FuelType fuelType
) {
}
