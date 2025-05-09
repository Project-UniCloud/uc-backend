package com.unicloudapp.user.infrastructure.rest;

import jakarta.validation.constraints.NotEmpty;

record AuthenticateRequest(
        @NotEmpty(message = "login cannot be empty")
        String login,
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

record CreateStudentRequest(
        @NotEmpty
        String userIndexNumber,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName
) {

}
