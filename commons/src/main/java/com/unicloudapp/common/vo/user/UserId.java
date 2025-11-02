package com.unicloudapp.common.vo.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

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

    @Override
    public String toString() {
        return value.toString();
    }
}
