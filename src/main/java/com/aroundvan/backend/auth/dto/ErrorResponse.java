package com.aroundvan.backend.auth.dto;

public record ErrorResponse(
        String message,
        String field
) {
}
