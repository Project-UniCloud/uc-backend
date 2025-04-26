package com.unicloudapp.users.infrastructure.rest;

import com.unicloudapp.users.application.ports.out.AuthenticationPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserRestController {

    private final AuthenticationPort authenticationPort;

    @PostMapping("/auth")
    ResponseEntity<Void> authenticate(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        authenticationPort.authenticate(authenticateRequest.username(),
                authenticateRequest.password());
        return ResponseEntity.status(200).build();
    }

    private record AuthenticateRequest(
            @NotEmpty(message = "username cannot be empty")
            @Size(min = 7, max = 7, message = "username must be 7 characters long")
            String username,
            @NotEmpty(message = "password cannot be mepty")
            String password
    ) {

    }
}