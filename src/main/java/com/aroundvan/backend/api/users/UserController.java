package com.aroundvan.backend.api.users;

import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserDTO;
import com.aroundvan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserDTO getCurrentUser(Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getHomeLocation(),
                user.getEmail()
        );
    }
}