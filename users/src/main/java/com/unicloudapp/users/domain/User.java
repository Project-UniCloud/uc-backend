package com.unicloudapp.users.domain;

import com.unicloudapp.commons.domain.Email;
import com.unicloudapp.commons.domain.FirstName;
import com.unicloudapp.commons.domain.LastLogin;
import com.unicloudapp.commons.domain.LastName;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class User {

    private final UserId userId;
    private final UserIndexNumber userIndexNumber;
    private FirstName firstName;
    private LastName lastName;
    private Email email;
    private LastLogin lastLogin;
    private final UserRole userRole;

    public void updateLastName(LastName newLastName) {
        this.lastName = newLastName;
    }

    public void updateFirstName(FirstName newFirstName) {
        this.firstName = newFirstName;
    }

    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }

    public void logIn(LastLogin lastLogin) {
        this.lastLogin = lastLogin;
    }

    public static User createUser(UUID userId,
                                  String indexNumber,
                                  String firstName,
                                  String lastName,
                                  String userRole
    ) {
        return User.builder()
                .userId(UserId.of(userId))
                .userIndexNumber(UserIndexNumber.of(indexNumber))
                .firstName(FirstName.of(firstName))
                .lastName(LastName.of(lastName))
                .email(Email.empty())
                .lastLogin(LastLogin.hasNeverBeenLoggedIn())
                .userRole(UserRole.valueOf(userRole))
                .build();
    }

    public static User of(UserId userId,
                          UserIndexNumber userIndexNumber,
                          FirstName firstName,
                          LastName lastName,
                          UserRole userRole,
                          Email email,
                          LastLogin lastLogin
    ) {
        return User.builder()
                .userId(userId)
                .userIndexNumber(userIndexNumber)
                .firstName(firstName)
                .lastName(lastName)
                .userRole(userRole)
                .email(email)
                .lastLogin(lastLogin)
                .build();
    }
}
