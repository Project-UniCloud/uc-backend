package com.unicloudapp.user.domain;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.LastLogin;
import com.unicloudapp.common.domain.LastName;
import com.unicloudapp.common.domain.user.UserId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFactory {

    public static User createUser(UUID userId,
                                  String indexNumber,
                                  String firstName,
                                  String lastName,
                                  UserRole.Type userRoleType
    ) {
        return buildUser(userId, indexNumber, firstName, lastName, userRoleType, null, null);
    }

    public static User createUser(UUID userId,
                                  String userIndexNumber,
                                  String firstName,
                                  String lastName,
                                  UserRole.Type userRoleType,
                                  String email,
                                  LocalDateTime lastLogin
    ) {
        return buildUser(userId, userIndexNumber, firstName, lastName, userRoleType, email, lastLogin);
    }

    private static User buildUser(UUID userId,
                                  String userIndexNumber,
                                  String firstName,
                                  String lastName,
                                  UserRole.Type userRoleType,
                                  String email,
                                  LocalDateTime lastLogin) {
        return User.builder()
                .userId(UserId.of(userId))
                .userIndexNumber(UserIndexNumber.of(userIndexNumber))
                .firstName(FirstName.of(firstName))
                .lastName(LastName.of(lastName))
                .email(Email.of(email))
                .lastLogin(LastLogin.of(lastLogin))
                .userRole(UserRole.of(userRoleType))
                .build();
    }
}
