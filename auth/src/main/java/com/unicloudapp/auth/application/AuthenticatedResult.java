package com.unicloudapp.auth.application;

import com.unicloudapp.common.vo.user.UserRole;

public record AuthenticatedResult(
        String token,
        UserRole role
) {}
