package com.unicloudapp.user.application;

import com.unicloudapp.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class UserDTO {

    private final UUID userId;
    private final String login;
    private final String firstName;
    private final String lastName;
    private final UserRole.Type userRole;
    private final String email;
    private final LocalDateTime lastLoginAt;
}
