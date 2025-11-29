package com.unicloudapp.common.vo.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRole {

    Type value;

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
