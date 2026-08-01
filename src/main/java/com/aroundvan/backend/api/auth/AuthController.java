package com.aroundvan.backend.api.auth;

import com.aroundvan.backend.auth.AuthService;
import com.aroundvan.backend.auth.dto.AuthResponse;
import com.aroundvan.backend.auth.dto.EmailRequest;
import com.aroundvan.backend.auth.dto.LoginRequest;
import com.aroundvan.backend.auth.dto.MessageResponse;
import com.aroundvan.backend.auth.dto.RegisterRequest;
import com.aroundvan.backend.auth.dto.ResetPasswordRequest;
import com.aroundvan.backend.auth.dto.TokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authService.register(request));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/confirm-email")
    public AuthResponse confirmEmail(@Valid @RequestBody TokenRequest request) {
        return authService.confirmEmail(request);
    }

    @PostMapping("/resend-confirmation")
    public MessageResponse resendConfirmation(@Valid @RequestBody EmailRequest request) {
        return authService.resendConfirmation(request);
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@Valid @RequestBody EmailRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
