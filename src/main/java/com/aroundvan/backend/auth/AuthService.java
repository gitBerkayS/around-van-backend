package com.aroundvan.backend.auth;

import com.aroundvan.backend.auth.dto.AuthResponse;
import com.aroundvan.backend.auth.dto.LoginRequest;
import com.aroundvan.backend.auth.dto.RegisterRequest;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "Username is already being used"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already being used"
            );
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        String token = tokenService.createToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                );

        var authentication =
                authenticationManager.authenticate(authenticationToken);

        String token = tokenService.createToken(authentication.getName());
        return new AuthResponse(token);
    }
}
