package com.aroundvan.backend.user;

import com.aroundvan.backend.gas.FuelType;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.dto.LocationDTO;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.user.dto.UpdateFuelPreferenceRequest;
import com.aroundvan.backend.user.dto.UpdateLocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocationService locationService;

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException(
                    "User is not authenticated"
            );
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );
    }

    @Transactional
    public UserDTO updateHomeLocation(UpdateLocationRequest request) {
        User user = getCurrentUser();

        Location homeLocation = locationService.resolveHomeLocation(
                user.getHomeLocation(),
                request.latitude(),
                request.longitude(),
                request.postalCodePrefix()
        );

        user.setHomeLocation(homeLocation);
        userRepository.save(user);

        return toDto(user);
    }

    @Transactional
    public UserDTO updateFuelPreference(UpdateFuelPreferenceRequest request) {
        User user = getCurrentUser();
        user.setPreferredFuelType(request.fuelType());
        userRepository.save(user);
        return toDto(user);
    }

    public UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                LocationDTO.from(user.getHomeLocation()),
                user.getEmail(),
                user.isEmailVerified(),
                user.getPreferredFuelType() != null
                        ? user.getPreferredFuelType()
                        : FuelType.REGULAR
        );
    }
}
