package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLogin {

    String value;

    public static UserLogin of(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User login cannot be null or empty");
        }
        return new UserLogin(value);
    }
}
