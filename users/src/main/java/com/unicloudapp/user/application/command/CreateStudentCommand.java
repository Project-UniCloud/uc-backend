package com.unicloudapp.user.application.command;

import com.unicloudapp.common.validation.StudentLogin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateStudentCommand(
        @StudentLogin
        @NotBlank
        @Size(min = 7, max = 7)
        String login,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email
) { }
