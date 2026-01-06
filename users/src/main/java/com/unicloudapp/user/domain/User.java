package com.unicloudapp.user.domain;

import com.unicloudapp.common.vo.Email;
import com.unicloudapp.common.vo.user.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class User {

    private final UserId userId;
    private final UserLogin userLogin;

    @Setter
    private FirstName firstName;

    @Setter
    private LastName lastName;

    @Setter
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
