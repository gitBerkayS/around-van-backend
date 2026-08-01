package com.aroundvan.backend.api;

import com.aroundvan.backend.auth.FieldTakenException;
import com.aroundvan.backend.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(FieldTakenException.class)
    public ResponseEntity<ErrorResponse> handleFieldTaken(FieldTakenException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage(), exception.getField()));
    }
}
