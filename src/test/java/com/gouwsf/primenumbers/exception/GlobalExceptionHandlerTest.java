package com.gouwsf.primenumbers.exception;

import com.gouwsf.primenumbers.model.ErrorResponse;
import com.jayway.jsonpath.internal.function.Parameter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ConstraintViolationException -> 400 with Validation Error title")
    void constraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("must be >= 2");

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
}
