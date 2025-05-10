package com.unicloudapp.common.domain;

import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Value
public class LastLoginAt {

    @Nullable
    LocalDateTime value;

    public static LastLoginAt of(LocalDateTime lastLogin) {
        if (lastLogin == null) {
            return neverBeenLoggedIn();
        }
        if (lastLogin.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Last login time is after now");
        }
        return new LastLoginAt(lastLogin);
    }

    public static LastLoginAt neverBeenLoggedIn() {
        return new LastLoginAt(null);
    }
}
