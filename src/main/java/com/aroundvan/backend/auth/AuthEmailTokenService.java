package com.aroundvan.backend.auth;

import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthEmailTokenService {

    private static final Duration CONFIRM_TTL = Duration.ofHours(24);
    private static final Duration RESET_TTL = Duration.ofHours(1);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    private final AuthEmailTokenRepository authEmailTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String issueToken(User user, AuthEmailTokenType type) {
        Instant now = Instant.now();

        authEmailTokenRepository.findFirstByUserAndTypeAndUsedAtIsNullOrderByCreatedAtDesc(user, type)
                .ifPresent(latest -> {
                    if (Duration.between(latest.getCreatedAt(), now).compareTo(RESEND_COOLDOWN) < 0) {
                        throw new ResponseStatusException(
                                HttpStatus.TOO_MANY_REQUESTS,
                                "Please wait before requesting another email"
                        );
                    }
                });

        authEmailTokenRepository.invalidateActiveTokens(user, type, now);

        String rawToken = generateRawToken();

        AuthEmailToken token = new AuthEmailToken();
        token.setUser(user);
        token.setType(type);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(now.plus(ttlFor(type)));
        token.setCreatedAt(now);

        authEmailTokenRepository.save(token);

        return rawToken;
    }

    @Transactional
    public User consumeToken(String rawToken, AuthEmailTokenType type) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }

        AuthEmailToken token = authEmailTokenRepository
                .findByTokenHashAndTypeAndUsedAtIsNull(hashToken(rawToken), type)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid or expired token"
                ));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or expired token"
            );
        }

        token.setUsedAt(Instant.now());
        authEmailTokenRepository.save(token);

        return token.getUser();
    }

    private static Duration ttlFor(AuthEmailTokenType type) {
        return type == AuthEmailTokenType.CONFIRM_EMAIL ? CONFIRM_TTL : RESET_TTL;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 not available", exception);
        }
    }
}
