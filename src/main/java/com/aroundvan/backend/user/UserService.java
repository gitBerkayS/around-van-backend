package com.aroundvan.backend.user;

import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
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
                request.longitude()
        );

        user.setHomeLocation(homeLocation);
        userRepository.save(user);

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getHomeLocation(),
                user.getEmail()
        );
    }
}
