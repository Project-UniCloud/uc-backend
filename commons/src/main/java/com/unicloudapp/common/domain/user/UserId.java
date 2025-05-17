package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    UUID value;

    public static UserId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        return new UserId(value);
    }

    public String toString() {
        return value.toString();
    }
}
