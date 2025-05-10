package com.unicloudapp.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserRole {

    private final Type value;

    public static UserRole of(Type userType) {
        if (userType == null) {
            throw new IllegalArgumentException();
        }
        return new UserRole(userType);
    }

    public enum Type {
        ADMIN, STUDENT, LECTURER
    }
}
