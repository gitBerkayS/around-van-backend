package com.aroundvan.backend.mail;

import com.aroundvan.backend.config.AppCorsProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ResendClient resendClient;
    private final AppCorsProperties corsProperties;

    private String confirmEmailTemplate;
    private String resetPasswordTemplate;

    @PostConstruct
    void loadTemplates() throws IOException {
        confirmEmailTemplate = readClasspath("mail/confirm-email.html");
        resetPasswordTemplate = readClasspath("mail/reset-password.html");
    }

    public void sendConfirmEmail(String toEmail, String username, String rawToken) {
        String link = corsProperties.frontendBaseUrl()
                + "/confirm-email?token=" + rawToken;

        String html = render(
                confirmEmailTemplate,
                username,
                link,
                "24 hours"
        );

        resendClient.sendEmail(toEmail, "Confirm your Vancouver Hub email", html);
    }

    public void sendPasswordResetEmail(String toEmail, String username, String rawToken) {
        String link = corsProperties.frontendBaseUrl()
                + "/reset-password?token=" + rawToken;

        String html = render(
                resetPasswordTemplate,
                username,
                link,
                "1 hour"
        );

        resendClient.sendEmail(toEmail, "Reset your Vancouver Hub password", html);
    }

    private static String render(
            String template,
            String username,
            String actionUrl,
            String expiryText
    ) {
        return template
                .replace("{{username}}", escape(username))
                .replace("{{actionUrl}}", escape(actionUrl))
                .replace("{{expiryText}}", escape(expiryText));
    }

    private static String readClasspath(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
