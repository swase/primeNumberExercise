package com.gouwsf.primenumbers.exception;

import com.gouwsf.primenumbers.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ConstraintViolationException -> 400 with Validation Error title")
    void constraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must be >= 2");

        var ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<Object> response = handler.handleConstraintViolation(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        var body = (ErrorResponse) response.getBody();
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getTitle()).isEqualTo("Validation Error");
        assertThat(body.getDescription()).contains("must be >= 2");
    }

    @Test
    @DisplayName("RuntimeException -> 500 with generic description")
    void runtimeException() {
        var ex = new RuntimeException("boom");

        ResponseEntity<Object> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        var body = (ErrorResponse) response.getBody();
        assertThat(body.getError()).isEqualTo("Internal Server Error");
        assertThat(body.getTitle()).isEmpty();
        assertThat(body.getDescription())
            .isEqualTo(GlobalExceptionHandler.INTERNAL_ERROR_MSG);
    }

    @Test
    @DisplayName("IllegalArgumentException -> 400 with Illegal argument title")
    void illegalArgument() {
        var ex = new IllegalArgumentException("bad arg");

        ResponseEntity<Object> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        var body = (ErrorResponse) response.getBody();
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getTitle()).isEqualTo("Illegal argument");
        assertThat(body.getDescription())
            .isEqualTo("Illegal argument during prime number generation");
    }

    @Test
    void whenRequiredTypeIsEnum_usesEnumMessage_andReturnsBadRequest() throws NoSuchMethodException {
        var method = DummyController.class.getDeclaredMethod("endpoint", DummyEnum.class, Integer.class);
        var param = new MethodParameter(method, 0); // the enum parameter "algorithm"

        var ex = new MethodArgumentTypeMismatchException(
                "INVALID",                 // provided value
                DummyEnum.class,           // required type (enum)
                "algorithm",               // parameter name
                param,                     // MethodParameter
                new IllegalArgumentException("No enum constant")
        );

        ResponseEntity<Object> response = handler.handleEnumConversionError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // We don't know your exact getEnumErrorMessage() format,
        // but it should at least mention the param/value or allowed values.
        assertThat(response.getBody().toString())
                .containsIgnoringCase("algorithm")
                .contains("INVALID"); // the rejected value should be surfaced
    }

    @Test
    void whenRequiredTypeIsNotEnum_usesDefaultMessage_andReturnsBadRequest() throws NoSuchMethodException {
        var method = DummyController.class.getDeclaredMethod("endpoint", DummyEnum.class, Integer.class);
        var param = new MethodParameter(method, 1); // the non-enum parameter "limit"

        var ex = new MethodArgumentTypeMismatchException(
                "abc",                     // provided value
                Integer.class,             // required type (not enum)
                "limit",                   // parameter name
                param,                     // MethodParameter
                new NumberFormatException("For input string: \"abc\"")
        );

        ResponseEntity<Object> response = handler.handleEnumConversionError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Your handler builds: "Invalid parameter: %s with value: %s"
        assertThat(response.getBody().toString())
                .contains("Invalid parameter");
    }

    enum DummyEnum { A, B }

    // A dummy target with parameters so we can build real MethodParameter objects
    static class DummyController {
        @SuppressWarnings("unused")
        void endpoint(DummyEnum algorithm, Integer limit) { /* no-op */ }
    }
}
