package com.unicloudapp.auth.infrastructure.rest;

import com.unicloudapp.auth.application.AuthCookieConfigurationProperties;
import com.unicloudapp.auth.application.AuthenticatedResult;
import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequiredArgsConstructor
class AuthorizationController {

    private final AuthenticationUseCase authenticationUseCase;
    private final AuthCookieConfigurationProperties authCookieConfigurationProperties;

    @PostMapping("/auth")
    protected ResponseEntity<AuthenticateResponse> authenticate(
            @Valid @RequestBody AuthenticateRequest authenticateRequest,
            HttpServletResponse response
    ) {
        AuthenticatedResult authenticatedResult = authenticationUseCase.authenticate(
                authenticateRequest.login(),
                authenticateRequest.password()
        );
        ResponseCookie cookie = ResponseCookie.from("jwt", authenticatedResult.token())
                .httpOnly(true)
                .secure(authCookieConfigurationProperties.secure())
                .path("/")
                .sameSite(authCookieConfigurationProperties.sameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok()
                .body(new AuthenticateResponse(authenticatedResult.role().getValue().toString()));
    }

    @PostMapping("/auth/logout")
    protected ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(authCookieConfigurationProperties.secure())
                .path("/")
                .sameSite(authCookieConfigurationProperties.sameSite())
                .maxAge(0)
                .build();

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();

        if (response != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
