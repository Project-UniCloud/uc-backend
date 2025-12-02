package com.unicloudapp.user.application.projection;

import com.unicloudapp.common.vo.user.UserRole;

import java.util.UUID;

public record UserRowProjection(
        UUID uuid,
        String email,
        String firstName,
        String lastName,
        String login,
        UserRole.Type role
) {}
