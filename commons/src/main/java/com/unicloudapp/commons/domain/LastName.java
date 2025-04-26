package com.unicloudapp.commons.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LastName {

    private final String value;

    public static LastName of(String value) throws IllegalArgumentException {
        if (value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new LastName(value);
    }
}
