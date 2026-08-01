package com.aroundvan.backend.mail;

import com.aroundvan.backend.mail.dto.ResendSendEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResendClient {

    private final RestClient resendRestClient;
    private final ResendProperties properties;

    public void sendEmail(String to, String subject, String html) {
        if (!properties.isConfigured()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Email delivery is not configured"
            );
        }

        ResendSendEmailRequest request = new ResendSendEmailRequest(
                properties.from(),
                List.of(to),
                subject,
                html
        );

        try {
            resendRestClient
                    .post()
                    .uri("/emails")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            String body = exception.getResponseBodyAsString();
            log.error(
                    "Resend send failed status={} from={} to={} body={}",
                    exception.getStatusCode().value(),
                    properties.from(),
                    to,
                    body
            );
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to send email: " + summarize(body, exception.getStatusCode().value()),
                    exception
            );
        }
    }

    private static String summarize(String body, int status) {
        if (body == null || body.isBlank()) {
            return "Resend HTTP " + status;
        }
        String trimmed = body.replaceAll("\\s+", " ").trim();
        return trimmed.length() > 300 ? trimmed.substring(0, 300) + "…" : trimmed;
    }
}
