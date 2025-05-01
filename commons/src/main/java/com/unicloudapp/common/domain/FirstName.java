package com.unicloudapp.common.domain;

import lombok.Value;

@Value
public class FirstName {

    String value;

    public static FirstName of(String value) throws IllegalArgumentException {
        if (value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new FirstName(value);
    }
}
