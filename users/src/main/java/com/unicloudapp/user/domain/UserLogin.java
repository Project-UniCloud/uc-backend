package com.unicloudapp.user.domain;

import lombok.Value;

@Value
public class UserLogin {

    String value;

    public static UserLogin of(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new UserLogin(value);
    }
}
