package com.unicloudapp.common.domain;

import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Value
public class LastLogin {

    @Nullable
    LocalDateTime lastLoginAt;

    public static LastLogin of(LocalDateTime lastLogin) {
        if (lastLogin == null) {
            return neverBeenLoggedIn();
        }
        if (lastLogin.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Last login time is after now");
        }
        return new LastLogin(lastLogin);
    }

    public static LastLogin neverBeenLoggedIn() {
        return new LastLogin(null);
    }
}
