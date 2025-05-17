package com.unicloudapp.user.domain;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserFactory {

    public User create(UUID userId,
                       String indexNumber,
                       String firstName,
                       String lastName,
                       UserRole.Type userRoleType
    ) {
        return buildUser(userId, indexNumber, firstName, lastName, userRoleType, null, null);
    }

    public User restore(UUID userId,
                        String userIndexNumber,
                        String firstName,
                        String lastName,
                        UserRole.Type userRoleType,
                        String email,
                        LocalDateTime lastLogin
    ) {
        return buildUser(userId, userIndexNumber, firstName, lastName, userRoleType, email, lastLogin);
    }

    private User buildUser(UUID userId,
                                  String userIndexNumber,
                                  String firstName,
                                  String lastName,
                                  UserRole.Type userRoleType,
                                  String email,
                                  LocalDateTime lastLogin) {
        return User.builder()
                .userId(UserId.of(userId))
                .userLogin(UserLogin.of(userIndexNumber))
                .firstName(FirstName.of(firstName))
                .lastName(LastName.of(lastName))
                .email(Email.of(email))
                .lastLoginAt(LastLoginAt.of(lastLogin))
                .userRole(UserRole.of(userRoleType))
                .build();
    }
}
