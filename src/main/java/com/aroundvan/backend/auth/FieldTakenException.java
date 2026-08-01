package com.aroundvan.backend.auth;

import lombok.Getter;

@Getter
public class FieldTakenException extends RuntimeException {

    private final String field;

    public FieldTakenException(String field, String message) {
        super(message);
        this.field = field;
    }
}
