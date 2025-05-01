package com.unicloudapp.user.infrastructure.rest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

record AuthenticateRequest(
        @NotEmpty(message = "username cannot be empty")
        @Size(min = 7, max = 7, message = "username must be 7 characters long")
        String username,
        @NotEmpty(message = "password cannot be mepty")
        String password
) {

}

record CreateLecturerRequest(
        @NotEmpty
        String userIndexNumber,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName
) {

}
