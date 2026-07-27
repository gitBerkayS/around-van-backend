package com.aroundvan.backend.api.users;

import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserDTO;
import com.aroundvan.backend.user.UserService;
import com.aroundvan.backend.user.dto.UpdateFuelPreferenceRequest;
import com.aroundvan.backend.user.dto.UpdateLocationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDTO getCurrentUser() {
        User user = userService.getCurrentUser();

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getHomeLocation(),
                user.getEmail(),
                user.getPreferredFuelType()
        );
    }

    @PutMapping("/me/location")
    public UserDTO updateHomeLocation(
            @Valid @RequestBody UpdateLocationRequest request) {
        return userService.updateHomeLocation(request);
    }

    @PutMapping("/me/fuel-preference")
    public UserDTO updateFuelPreference(
            @Valid @RequestBody UpdateFuelPreferenceRequest request) {
        return userService.updateFuelPreference(request);
    }
}
