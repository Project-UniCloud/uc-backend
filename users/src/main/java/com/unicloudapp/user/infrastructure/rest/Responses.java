package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.user.domain.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
record LecturerCreatedResponse(UUID lecturerId) {

}

@Builder
record StudentCreatedResponse(UUID studentId) {

}

@Builder
record UserFoundResponse(
        @NotNull UUID userId,
        @NotBlank String login,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Nullable String email,
        @Nullable LocalDateTime lastLoginAt,
        @NotNull UserRole.Type userRole
) {

}
