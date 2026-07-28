package com.aroundvan.backend.user;

import com.aroundvan.backend.gas.FuelType;
import com.aroundvan.backend.location.dto.LocationDTO;

public record UserDTO(
        Long id,
        String username,
        LocationDTO location,
        String email,
        FuelType preferredFuelType
) {
}
