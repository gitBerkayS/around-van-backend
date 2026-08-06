package com.aroundvan.backend.usage;

import com.aroundvan.backend.config.AppUsageProperties;
import com.aroundvan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsageTrackingService {

    private final AppUsageProperties properties;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, Instant> lastActivityLog = new ConcurrentHashMap<>();

    public void trackAuthenticatedRequest(String username, String method, String path) {
        if (isMutating(method)) {
            log.info("usage.action user={} method={} path={}", username, method, path);
            lastActivityLog.put(username, Instant.now());
            touchLastActive(username);
            return;
        }

        if (claimActivityWindow(username)) {
            log.info("usage.active user={} method={} path={}", username, method, path);
            touchLastActive(username);
        }
    }

    public void markLogin(long userId, String username) {
        lastActivityLog.put(username, Instant.now());
        log.info("auth.login userId={} username={}", userId, username);
        touchLastActive(username);
    }

    public void markRegister(long userId, String username) {
        log.info("auth.register userId={} username={}", userId, username);
    }

    public void markEmailConfirmed(long userId, String username) {
        lastActivityLog.put(username, Instant.now());
        log.info("auth.email_confirmed userId={} username={}", userId, username);
        touchLastActive(username);
    }

    public void markPasswordReset(long userId, String username) {
        log.info("auth.password_reset userId={} username={}", userId, username);
    }

    private boolean claimActivityWindow(String username) {
        Instant now = Instant.now();
        Instant[] claimed = new Instant[1];

        lastActivityLog.compute(username, (key, previous) -> {
            if (previous != null
                    && previous.plus(properties.activityLogInterval()).isAfter(now)) {
                return previous;
            }
            claimed[0] = now;
            return now;
        });

        return claimed[0] != null;
    }

    private void touchLastActive(String username) {
        try {
            userRepository.touchLastActiveAt(username, Instant.now());
        } catch (RuntimeException exception) {
            log.warn("Failed to update last_active_at for user={}", username, exception);
        }
    }

    private static boolean isMutating(String method) {
        return !"GET".equalsIgnoreCase(method)
                && !"HEAD".equalsIgnoreCase(method)
                && !"OPTIONS".equalsIgnoreCase(method);
    }
}
