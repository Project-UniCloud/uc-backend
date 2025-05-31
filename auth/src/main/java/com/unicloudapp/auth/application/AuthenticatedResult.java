package com.unicloudapp.auth.application;

import com.unicloudapp.common.domain.user.UserRole;

public record AuthenticatedResult(
        String token,
        UserRole role
) {}
