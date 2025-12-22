package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.common.vo.user.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
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
        @NotNull Set<UserRole.Type> userRoles
) {

}

record LecturerFullNameResponse(
        @NotNull UUID userId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String login,
        @NotBlank String email
) {
}
