package com.unicloudapp.auth.application;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class CookieClearingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthCookieConfigurationProperties authCookieConfigurationProperties;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(authCookieConfigurationProperties.secure())
                .path("/")
                .sameSite(authCookieConfigurationProperties.sameSite())
                .maxAge(0)
                .build();

        ResponseCookie rolesCookie = ResponseCookie.from("roles", "")
                .httpOnly(true)
                .secure(authCookieConfigurationProperties.secure())
                .path("/")
                .sameSite(authCookieConfigurationProperties.sameSite())
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, rolesCookie.toString());
    }
}
