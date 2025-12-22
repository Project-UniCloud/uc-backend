package com.unicloudapp.common.vo.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LastName {

    String value;

    public static LastName of(@Nullable String value) throws IllegalArgumentException {
        if (value == null) {
            throw new NullPointerException("Last name cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        return new LastName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
