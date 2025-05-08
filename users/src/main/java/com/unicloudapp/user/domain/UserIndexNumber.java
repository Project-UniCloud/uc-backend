package com.unicloudapp.user.domain;

import lombok.Value;

@Value
public class UserIndexNumber {

    String value;

    public static UserIndexNumber of(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new UserIndexNumber(value);
    }
}
