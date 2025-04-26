package com.unicloudapp.commons.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LastLogin {

    @Nullable
    private final LocalDateTime lastLoginAt;

    public static LastLogin of(LocalDateTime lastLogin) {
        if (lastLogin.isAfter(LocalDateTime.now())) {
            hasNeverBeenLoggedIn();
        }
        return new LastLogin(lastLogin);
    }

    public static LastLogin hasNeverBeenLoggedIn() {
        return new LastLogin(null);
    }
}
