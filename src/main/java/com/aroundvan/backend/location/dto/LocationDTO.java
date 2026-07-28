package com.aroundvan.backend.location.dto;

import com.aroundvan.backend.location.Location;

public record LocationDTO(
        Long id,
        double latitude,
        double longitude,
        String postalCodePrefix
) {
    public static LocationDTO from(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationDTO(
                location.getId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getPostalCodePrefix()
        );
    }
}
