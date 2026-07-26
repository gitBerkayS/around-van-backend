package com.aroundvan.backend.api.users;

import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserDTO;
import com.aroundvan.backend.user.UserService;
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
                user.getEmail()
        );
    }

    // accepts browser geolocation or manual lat long
    @PutMapping("/me/location")
    public UserDTO updateHomeLocation(
            @Valid @RequestBody UpdateLocationRequest request) {
        return userService.updateHomeLocation(request);
    }
}
