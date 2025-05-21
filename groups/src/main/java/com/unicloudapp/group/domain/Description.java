package com.unicloudapp.group.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Description {

    String value;

    public static Description of(String value) {
        return new Description(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }

}
