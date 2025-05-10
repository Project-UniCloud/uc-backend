package com.unicloudapp.user.application;

import com.unicloudapp.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDTO(
        @NotNull UUID userId,
        @NotBlank String login,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull UserRole.Type userRole,
        @Nullable String email,
        @Nullable LocalDateTime lastLoginAt
) {
}