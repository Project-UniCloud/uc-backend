package com.unicloudapp.auth.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unicloudapp.auth.application.AuthCookieConfigurationProperties;
import com.unicloudapp.auth.application.AuthenticatedResult;
import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import com.unicloudapp.common.vo.user.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthorizationControllerTest {

    private AuthorizationController authorizationController;

    @Mock
    private AuthenticationUseCase authenticationUseCase;

    @Mock
    private AuthCookieConfigurationProperties authCookieConfigurationProperties;

    @BeforeEach
    void setUp() {
        authorizationController = new AuthorizationController(authenticationUseCase, authCookieConfigurationProperties);
    }

    @Test
    void shouldReturnJwtAndRolesCookiesOnSuccessfulAuthentication() {
        // given
        AuthenticateRequest request = new AuthenticateRequest("user", "password");
        AuthenticatedResult result = new AuthenticatedResult(
                "test-token", UserRole.of(Set.of(UserRole.Type.STUDENT, UserRole.Type.LECTURER)));
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authenticationUseCase.authenticate(anyString(), anyString())).thenReturn(result);
        when(authCookieConfigurationProperties.secure()).thenReturn(false);
        when(authCookieConfigurationProperties.sameSite()).thenReturn("Lax");

        // when
        ResponseEntity<@NotNull AuthenticateResponse> responseEntity = authorizationController.authenticate(request, response);

        // then
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), headerValueCaptor.capture());

        List<String> cookies = headerValueCaptor.getAllValues();
        assertThat(cookies).anyMatch(c -> c.contains("jwt=test-token") && c.contains("HttpOnly"));
        assertThat(cookies).anyMatch(c -> c.contains("roles=STUDENT-LECTURER") && !c.contains("HttpOnly"));
    }
}
