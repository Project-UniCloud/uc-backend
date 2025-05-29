package com.unicloudapp.user.domain;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.*;

public class UserFactory {

    public User create(UserId userId,
                       UserLogin userLogin,
                       FirstName firstName,
                       LastName lastName,
                       UserRole userRole
    ) {
        return buildUser(
                userId,
                userLogin,
                firstName,
                lastName,
                userRole,
                Email.empty(),
                LastLoginAt.empty()
        );
    }

    public User restore(UserId userId,
                        UserLogin userIndexNumber,
                        FirstName firstName,
                        LastName lastName,
                        UserRole userRoleType,
                        Email email,
                        LastLoginAt lastLogin
    ) {
        return buildUser(
                userId,
                userIndexNumber,
                firstName,
                lastName,
                userRoleType,
                email,
                lastLogin
        );
    }

    private User buildUser(UserId userId,
                           UserLogin userIndexNumber,
                           FirstName firstName,
                           LastName lastName,
                           UserRole userRoleType,
                           Email email,
                           LastLoginAt lastLogin) {
        return User.builder()
                .userId(userId)
                .userLogin(userIndexNumber)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLoginAt(lastLogin)
                .userRole(userRoleType)
                .build();
    }
}
