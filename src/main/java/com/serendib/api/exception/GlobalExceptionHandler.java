package com.serendib.api.exception;

import com.serendib.api.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice // @RestControllerAdvice: "intercept exceptions from ALL controllers"
@Slf4j// @Slf4j = Lombok gives a 'log' object for logging
public class GlobalExceptionHandler {

    // Handle 404 — Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Handle 400 — Business rule violations
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex) {

        log.warn("Business rule violation: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)        // HTTP 400
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Handle 400 — Validation errors (@Valid failures)
    // This fires when @Valid finds problems in your RequestDTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect all field errors into a map
        // ex: { "email": "Must be valid email", "name": "Required" }
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        log.warn("Validation failed: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)        // HTTP 400
                .body(ApiResponse.error("Validation failed", errors));
    }

    // Handle 500 — Any unexpected exception
    // Safety net — catches everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {

        // Log the full stack trace for debugging
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // HTTP 500
                .body(ApiResponse.error(
                        "Something went wrong. Please try again later."
                ));
        // Note: don't expose ex.getMessage() to client here
        // — could leak internal details
    }
}