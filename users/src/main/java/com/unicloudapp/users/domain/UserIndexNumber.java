package com.unicloudapp.users.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserIndexNumber {

    private final String value;

    public static UserIndexNumber of(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new UserIndexNumber(value);
    }
}
