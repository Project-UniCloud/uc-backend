package com.unicloudapp.user.application;

import com.unicloudapp.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Builder
@Getter
public class UserDTO {

    @Nullable
    private final UUID userId;
    private final String userIndexNumber;
    private final String firstName;
    private final String lastName;
    private final UserRole.Type userRoleType;
}
