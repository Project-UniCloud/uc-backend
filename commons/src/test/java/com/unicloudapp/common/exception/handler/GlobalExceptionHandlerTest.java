package com.unicloudapp.common.exception.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("defaultHandleMethod builds ProblemDetail with provided title, detail and status")
    void defaultHandleMethodBuildsProblemDetail() {
        ResponseEntity<ProblemDetail> response = handler.defaultHandleMethod(
                "Something went wrong", "Bad request", HttpStatus.BAD_REQUEST);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad request", response.getBody().getTitle());
        assertEquals("Something went wrong", response.getBody().getDetail());
    }

    @Test
    @DisplayName("handle(MethodArgumentNotValidException) aggregates field errors and returns BAD_REQUEST")
    void handleMethodArgumentNotValidAggregatesErrors() throws NoSuchMethodException {
        // Prepare BindingResult with two field errors
        Dummy target = new Dummy();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "dummy");
        bindingResult.addError(new FieldError("dummy", "email", "must not be blank"));
        bindingResult.addError(new FieldError("dummy", "name", "must not be null"));

        // Build a MethodParameter for the exception (required by Spring API)
        Method method = Dummy.class.getDeclaredMethod("dummy", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad request", response.getBody().getTitle());
        assertEquals("email - must not be blank; name - must not be null", response.getBody().getDetail());
    }

    @Test
    @DisplayName("handle(AccessDeniedException) returns FORBIDDEN with 'Forbidden' title")
    void handleAccessDenied() {
        ResponseEntity<ProblemDetail> response = handler.handle(new AccessDeniedException("denied"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Forbidden", response.getBody().getTitle());
        assertEquals("denied", response.getBody().getDetail());
    }

    // Helper class for reflection in test
    static class Dummy {
        @SuppressWarnings("unused")
        void dummy(String value) {}
    }
}
