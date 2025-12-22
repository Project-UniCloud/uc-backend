package com.unicloudapp.common.vo.user;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

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
