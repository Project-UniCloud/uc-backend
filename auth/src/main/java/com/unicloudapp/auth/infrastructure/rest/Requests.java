package com.unicloudapp.auth.infrastructure.rest;

import jakarta.validation.constraints.NotEmpty;

record AuthenticateRequest(
        @NotEmpty(message = "login cannot be empty")
        String login,
        @NotEmpty(message = "password cannot be mepty")
        String password
) {

}
