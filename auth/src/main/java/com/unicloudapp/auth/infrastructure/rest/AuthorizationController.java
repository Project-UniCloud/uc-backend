package com.unicloudapp.auth.infrastructure.rest;

import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class AuthorizationController {

    private final AuthenticationUseCase authenticationPort;

    @PostMapping("/auth")
    ResponseEntity<String> authenticate(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        String authenticated = authenticationPort.authenticate(authenticateRequest.login(),
                authenticateRequest.password()
        );
        return ResponseEntity.status(200)
                .body(authenticated);
    }
}
