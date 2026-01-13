package com.unicloudapp.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

class CookieClearingAuthenticationEntryPointTest {

    private CookieClearingAuthenticationEntryPoint entryPoint;
    private AuthCookieConfigurationProperties authCookieConfigurationProperties;

    @BeforeEach
    void setUp() {
        authCookieConfigurationProperties = mock(AuthCookieConfigurationProperties.class);
        entryPoint = new CookieClearingAuthenticationEntryPoint(authCookieConfigurationProperties);
    }

    @Test
    void shouldSetUnauthorizedStatusAndClearCookies() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(authCookieConfigurationProperties.secure()).thenReturn(true);
        when(authCookieConfigurationProperties.sameSite()).thenReturn("Lax");

        // when
        entryPoint.commence(request, response, authException);

        // then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), headerValueCaptor.capture());

        List<String> cookies = headerValueCaptor.getAllValues();
        assertThat(cookies).anyMatch(c -> c.contains("jwt=") && c.contains("Max-Age=0"));
        assertThat(cookies).anyMatch(c -> c.contains("roles=") && c.contains("Max-Age=0"));
    }
}
