package com.unicloudapp.users.domain;

import com.unicloudapp.commons.domain.Email;
import com.unicloudapp.commons.domain.FirstName;
import com.unicloudapp.commons.domain.LastLogin;
import com.unicloudapp.commons.domain.LastName;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class User {

    private final UserId userId;
    private final FirstName firstName;
    private final LastName lastName;
    private final Email email;
    private final LastLogin lastLogin;
    private final UserRole userRole;

    public static User createUser(String userId,
                           String firstName,
                           String lastName,
                           String userRole
    ) {
        return User.builder()
                .userId(UserId.of(userId))
                .firstName(FirstName.of(firstName))
                .lastName(LastName.of(lastName))
                .email(Email.empty())
                .lastLogin(LastLogin.hasNeverBeenLoggedIn())
                .userRole(UserRole.valueOf(userRole))
                .build();
    }

    public static User of(UserId userId,
                          FirstName firstName,
                          LastName lastName,
                          UserRole userRole,
                          Email email,
                          LastLogin lastLogin
    ) {
        return User.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .userRole(userRole)
                .email(email)
                .lastLogin(lastLogin)
                .build();
    }
}
