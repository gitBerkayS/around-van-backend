package com.aroundvan.backend.usage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UsageTrackingFilter extends OncePerRequestFilter {

    private static final String MDC_USER = "user";

    private final UsageTrackingService usageTrackingService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || "/error".equals(path)
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticatedUser(authentication)) {
            String username = authentication.getName();
            MDC.put(MDC_USER, username);
            try {
                usageTrackingService.trackAuthenticatedRequest(
                        username,
                        request.getMethod(),
                        request.getRequestURI()
                );
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(MDC_USER);
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getName() != null
                && !"anonymousUser".equals(authentication.getName());
    }
}
