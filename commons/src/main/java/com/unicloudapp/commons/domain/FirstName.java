package com.unicloudapp.commons.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FirstName {

    private final String value;

    public static FirstName of(String value) throws IllegalArgumentException {
        if (value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new FirstName(value);
    }
}
