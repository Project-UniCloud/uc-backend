package com.unicloudapp.auth.infrastructure.rest;

import com.unicloudapp.auth.application.AuthenticatedResult;
import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequiredArgsConstructor
class AuthorizationController {

    private final AuthenticationUseCase authenticationUseCase;

    @PostMapping("/auth")
    ResponseEntity<AuthenticateResponse> authenticate(
            @Valid @RequestBody AuthenticateRequest authenticateRequest,
            HttpServletResponse response
    ) {
        AuthenticatedResult authenticatedResult = authenticationUseCase.authenticate(
                authenticateRequest.login(),
                authenticateRequest.password()
        );
        ResponseCookie cookie = ResponseCookie.from("jwt", authenticatedResult.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok()
                .body(new AuthenticateResponse(authenticatedResult.role().getValue().toString()));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
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
