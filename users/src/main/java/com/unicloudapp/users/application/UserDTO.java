package com.unicloudapp.users.application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDTO {

    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String userRole;
}
