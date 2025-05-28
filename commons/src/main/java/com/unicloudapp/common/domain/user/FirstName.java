package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FirstName {

    String value;

    public static FirstName of(String value) throws IllegalArgumentException {
        if (value.isBlank()) {
            throw new IllegalArgumentException("TODO");
        }
        return new FirstName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
