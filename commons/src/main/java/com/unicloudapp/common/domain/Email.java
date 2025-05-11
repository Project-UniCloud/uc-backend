package com.unicloudapp.common.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {

    String value;

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            return new Email("");
        }
        //TODO check email regex
        return new Email(value);
    }

    public static Email empty() {
        return new Email("");
    }

    public boolean isEmpty() {
        return value.isBlank();
    }
}
