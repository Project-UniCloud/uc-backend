package com.unicloudapp.users.application;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class UserDTO {

    private final UUID userId;
    private final String userIndexNumber;
    private final String firstName;
    private final String lastName;
    private final String userRole;
}
