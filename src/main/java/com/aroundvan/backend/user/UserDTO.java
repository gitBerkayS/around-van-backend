package com.aroundvan.backend.user;

import com.aroundvan.backend.location.Location;

public record UserDTO(Long id, String username, Location location, String email) {
}
