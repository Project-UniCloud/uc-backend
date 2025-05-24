package com.unicloudapp.user.domain;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.user.*;
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
    private LastLoginAt lastLoginAt;
    private final UserRole userRole;

    public void logIn(LastLoginAt lastLoginAt) {
        if (this.lastLoginAt.isAfter(lastLoginAt)) {
            throw new IllegalArgumentException("Last login time cannot be null");
        }
        this.lastLoginAt = lastLoginAt;
    }
}
