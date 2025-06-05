package com.unicloudapp.user.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateStudentCommand(
        @NotBlank String login,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email
) { }
