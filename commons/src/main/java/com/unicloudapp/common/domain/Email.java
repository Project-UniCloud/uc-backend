package com.unicloudapp.common.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.regex.Pattern;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {

    private static final Pattern emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    String value;

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            return new Email("");
        }
        if (!emailPattern.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return new Email(value);
    }

    public static Email empty() {
        return new Email("");
    }

    public boolean isEmpty() {
        return value.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }
}
