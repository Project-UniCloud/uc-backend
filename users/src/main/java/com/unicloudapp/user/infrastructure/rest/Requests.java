package com.unicloudapp.user.infrastructure.rest;

import jakarta.validation.constraints.NotEmpty;

record CreateLecturerRequest(
        @NotEmpty(message = "userIndexNumber cannot be empty")
        String userIndexNumber,
        @NotEmpty(message = "firstName cannot be empty")
        String firstName,
        @NotEmpty(message = "lastName cannot be empty")
        String lastName
) {

}

record CreateStudentRequest(
        @NotEmpty(message = "userIndexNumber cannot be empty")
        String userIndexNumber,
        @NotEmpty(message = "firstName cannot be empty")
        String firstName,
        @NotEmpty(message = "lastName cannot be empty")
        String lastName
) {

}
