package com.gouwsf.primenumbers.exception;

import com.gouwsf.primenumbers.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler for handling error response.
 *
 * Ensures consistent response using generated ErrorResponse model generated from OAS
 * number generation to {@link ErrorResponse}.
 */
@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    public static String INTERNAL_ERROR_MSG = "Something went wrong. We are working hard to fix the issue";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return handleErrorResponse(ex, HttpStatus.BAD_REQUEST, message, "Validation Error");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Constraint violation");

        return handleErrorResponse(ex, HttpStatus.BAD_REQUEST, message, "Validation Error");
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return handleErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MSG, "");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return handleErrorResponse(ex, HttpStatus.BAD_REQUEST, "Illegal argument during prime number generation","Illegal argument");
    }

    private ResponseEntity<Object> handleErrorResponse(Exception ex, HttpStatus status, String description, String errTitle) {
        log.error("Exception thrown. cause: {} message: {}", ex.getCause(), ex.getMessage());

        var errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .title(errTitle)
                .description(description)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
