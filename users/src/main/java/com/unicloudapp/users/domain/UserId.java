package com.unicloudapp.users.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserId {

    private final String value;

    public static UserId of(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
        return new UserId(value);
    }
}
