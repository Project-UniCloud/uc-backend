package com.unicloudapp.user.domain;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.LastLogin;
import com.unicloudapp.common.domain.LastName;
import com.unicloudapp.common.domain.user.UserId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class User {

    private final UserId userId;
    private final UserLogin userLogin;
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
}
