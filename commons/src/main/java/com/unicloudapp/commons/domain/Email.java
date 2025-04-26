package com.unicloudapp.commons.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Email {

    private final String value;

    public static Email of(String value) {
        if (value.isBlank()) {
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
