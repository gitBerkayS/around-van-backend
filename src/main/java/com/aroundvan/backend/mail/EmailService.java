package com.aroundvan.backend.mail;

import com.aroundvan.backend.config.AppCorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ResendClient resendClient;
    private final AppCorsProperties corsProperties;

    public void sendConfirmEmail(String toEmail, String username, String rawToken) {
        String link = corsProperties.frontendBaseUrl()
                + "/confirm-email?token=" + rawToken;

        String html = """
                <p>Hi %s,</p>
                <p>Confirm your Around Van account by opening this link:</p>
                <p><a href="%s">Confirm email</a></p>
                <p>This link expires in 24 hours.</p>
                <p>If you did not create this account, you can ignore this email.</p>
                """.formatted(escape(username), link);

        resendClient.sendEmail(toEmail, "Confirm your Around Van email", html);
    }

    public void sendPasswordResetEmail(String toEmail, String username, String rawToken) {
        String link = corsProperties.frontendBaseUrl()
                + "/reset-password?token=" + rawToken;

        String html = """
                <p>Hi %s,</p>
                <p>Reset your Around Van password by opening this link:</p>
                <p><a href="%s">Reset password</a></p>
                <p>This link expires in 1 hour.</p>
                <p>If you did not request a reset, you can ignore this email.</p>
                """.formatted(escape(username), link);

        resendClient.sendEmail(toEmail, "Reset your Around Van password", html);
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
