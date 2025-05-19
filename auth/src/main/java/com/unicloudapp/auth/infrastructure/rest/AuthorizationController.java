package com.unicloudapp.auth.infrastructure.rest;

import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class AuthorizationController {

    private final AuthenticationUseCase authenticationPort;

    @PostMapping("/auth")
    ResponseEntity<Void> authenticate(
            @Valid @RequestBody AuthenticateRequest authenticateRequest,
            HttpServletResponse response
    ) {
        String jwtToken = authenticationPort.authenticate(authenticateRequest.login(),
                authenticateRequest.password()
        );
        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }
}
