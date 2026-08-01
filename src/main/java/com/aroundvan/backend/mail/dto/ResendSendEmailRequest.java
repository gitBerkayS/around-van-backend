package com.aroundvan.backend.mail.dto;

import java.util.List;

public record ResendSendEmailRequest(
        String from,
        List<String> to,
        String subject,
        String html
) {
}
