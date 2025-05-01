package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserId {

    private final UUID value;

    public static UserId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        return new UserId(value);
    }
}
