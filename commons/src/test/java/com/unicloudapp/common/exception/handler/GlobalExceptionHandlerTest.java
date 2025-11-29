package com.unicloudapp.common.exception.handler;

import com.unicloudapp.common.exception.user.UserNotFoundException;
import com.unicloudapp.common.vo.user.UserId;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("IllegalArgumentException -> 400 Bad request with message")
    void handleIllegalArgumentException() {
        String message = "Illegal argument provided";
        ResponseEntity<ProblemDetail> resp = handler.handle(new IllegalArgumentException(message));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Bad request");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("IllegalStateException -> 400 Bad request with message")
    void handleIllegalStateException() {
        String message = "Illegal state";
        ResponseEntity<ProblemDetail> resp = handler.handle(new IllegalStateException(message));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Bad request");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("Generic Exception -> 400 Unknown error with message")
    void handleGenericException() {
        String message = "Something went wrong";
        ResponseEntity<ProblemDetail> resp = handler.handle(new Exception(message));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Unknown error");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("AccessDeniedException -> 403 Forbidden with message")
    void handleAccessDeniedException() {
        String message = "Denied";
        ResponseEntity<ProblemDetail> resp = handler.handle(new AccessDeniedException(message));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Forbidden");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("UserNotFoundException -> 404 User not found with message")
    void handleUserNotFoundException() {
        var id = UUID.randomUUID();
        String expected = "User with id %s not found".formatted(UserId.of(id));
        ResponseEntity<ProblemDetail> resp = handler.handle(new UserNotFoundException(UserId.of(id)));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("User not found");
        assertThat(resp.getBody().getDetail()).isEqualTo(expected);
    }

    @Test
    @DisplayName("ConstraintViolationException -> 400 Bad request with message")
    void handleConstraintViolationException() {
        String message = "constraint failed";
        // Use the actual Hibernate exception; message is enough for the handler
        ConstraintViolationException ex = new ConstraintViolationException(message, new SQLException("sql"), "constraint_name");

        ResponseEntity<ProblemDetail> resp = handler.handle(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Bad request");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("HttpMessageNotReadableException -> 400 Wrong request format with message")
    void handleHttpMessageNotReadableException() {
        String message = "Malformed JSON";
        ResponseEntity<ProblemDetail> resp = handler.handle(new HttpMessageNotReadableException(message, null));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Wrong request format");
        assertThat(resp.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    @DisplayName("MethodArgumentNotValidException -> 400 Bad request with joined field errors")
    void handleMethodArgumentNotValidException() {
        // Mock BindingResult to return a couple of FieldErrors
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError e1 = new FieldError("obj", "email", "must be a well-formed email address");
        FieldError e2 = new FieldError("obj", "age", "must be greater than or equal to 18");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(e1, e2));

        // Mock MethodArgumentNotValidException to return our BindingResult
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ProblemDetail> resp = handler.handle(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getTitle()).isEqualTo("Bad request");
        assertThat(resp.getBody().getDetail())
                .isEqualTo("email - must be a well-formed email address; age - must be greater than or equal to 18");
    }
}
