package com.unicloudapp.common.vo.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FirstName {

    String value;

    public static FirstName of(@Nullable String value) throws IllegalArgumentException {
        if (value == null) {
            throw new NullPointerException("First name cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        return new FirstName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
