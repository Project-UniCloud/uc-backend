package com.unicloudapp.common.domain;

import lombok.Value;

@Value
public class LastName {

    String value;

    public static LastName of(String value) throws IllegalArgumentException {
        if (value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new LastName(value);
    }
}
