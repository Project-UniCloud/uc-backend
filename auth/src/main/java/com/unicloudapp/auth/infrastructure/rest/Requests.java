package com.unicloudapp.auth.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;

record AuthenticateRequest(
        @NotBlank
        String login,
        @NotBlank
        String password
) {

}
