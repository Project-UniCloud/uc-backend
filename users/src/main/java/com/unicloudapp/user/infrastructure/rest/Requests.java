package com.unicloudapp.user.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record CreateLecturerRequest(
        @NotBlank
        @Size(min = 1, max = 32)
        String userIndexNumber,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {

}

record CreateStudentRequest(
        @NotBlank
        String userIndexNumber,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {

}
