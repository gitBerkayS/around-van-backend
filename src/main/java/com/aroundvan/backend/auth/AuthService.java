package com.aroundvan.backend.auth;

import com.aroundvan.backend.auth.dto.AuthResponse;
import com.aroundvan.backend.auth.dto.EmailRequest;
import com.aroundvan.backend.auth.dto.LoginRequest;
import com.aroundvan.backend.auth.dto.MessageResponse;
import com.aroundvan.backend.auth.dto.RegisterRequest;
import com.aroundvan.backend.auth.dto.ResetPasswordRequest;
import com.aroundvan.backend.auth.dto.TokenRequest;
import com.aroundvan.backend.mail.EmailService;
import com.aroundvan.backend.mail.ResendProperties;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String GENERIC_EMAIL_SENT =
            "If an account exists for that email, we sent instructions";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthEmailTokenService authEmailTokenService;
    private final EmailService emailService;
    private final ResendProperties resendProperties;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
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
        user.setEmailVerified(false);

        userRepository.save(user);

        String rawToken = authEmailTokenService.issueToken(
                user,
                AuthEmailTokenType.CONFIRM_EMAIL
        );
        emailService.sendConfirmEmail(user.getEmail(), user.getUsername(), rawToken);

        return new MessageResponse(
                "Account created. Check your email to confirm your address before logging in"
        );
    }

    public AuthResponse login(LoginRequest request) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                );

        var authentication =
                authenticationManager.authenticate(authenticationToken);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid username or password"
                ));

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Confirm your email before logging in"
            );
        }

        String token = tokenService.createToken(authentication.getName());
        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse confirmEmail(TokenRequest request) {
        User user = authEmailTokenService.consumeToken(
                request.token(),
                AuthEmailTokenType.CONFIRM_EMAIL
        );

        user.setEmailVerified(true);
        userRepository.save(user);

        return new AuthResponse(tokenService.createToken(user.getUsername()));
    }

    @Transactional
    public MessageResponse resendConfirmation(EmailRequest request) {
        requireEmailConfigured();

        userRepository.findByEmail(request.email())
                .filter(user -> !user.isEmailVerified())
                .ifPresent(this::sendConfirmEmailQuietly);

        return new MessageResponse(GENERIC_EMAIL_SENT);
    }

    @Transactional
    public MessageResponse forgotPassword(EmailRequest request) {
        requireEmailConfigured();

        userRepository.findByEmail(request.email())
                .ifPresent(this::sendPasswordResetEmailQuietly);

        return new MessageResponse(GENERIC_EMAIL_SENT);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = authEmailTokenService.consumeToken(
                request.token(),
                AuthEmailTokenType.RESET_PASSWORD
        );

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new MessageResponse("Password updated. You can log in with your new password");
    }

    private void requireEmailConfigured() {
        if (!resendProperties.isConfigured()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Email delivery is not configured"
            );
        }
    }

    private void sendConfirmEmailQuietly(User user) {
        try {
            String rawToken = authEmailTokenService.issueToken(
                    user,
                    AuthEmailTokenType.CONFIRM_EMAIL
            );
            emailService.sendConfirmEmail(
                    user.getEmail(),
                    user.getUsername(),
                    rawToken
            );
        } catch (ResponseStatusException exception) {
            if (exception.getStatusCode() != HttpStatus.TOO_MANY_REQUESTS) {
                throw exception;
            }
        }
    }

    private void sendPasswordResetEmailQuietly(User user) {
        try {
            String rawToken = authEmailTokenService.issueToken(
                    user,
                    AuthEmailTokenType.RESET_PASSWORD
            );
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    user.getUsername(),
                    rawToken
            );
        } catch (ResponseStatusException exception) {
            if (exception.getStatusCode() != HttpStatus.TOO_MANY_REQUESTS) {
                throw exception;
            }
        }
    }
}
