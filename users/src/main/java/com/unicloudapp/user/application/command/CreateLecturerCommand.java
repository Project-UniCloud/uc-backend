package com.unicloudapp.user.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateLecturerCommand(
        @NotBlank String login,
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
