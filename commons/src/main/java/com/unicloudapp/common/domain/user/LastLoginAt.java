package com.unicloudapp.common.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LastLoginAt {

    @Nullable
    LocalDateTime value;

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isAfter(LastLoginAt lastLoginAt) {
        if (lastLoginAt == null) {
            return false;
        }
        if (this.value == null) {
            return false;
        }
        return this.value.isAfter(lastLoginAt.value);
    }

    public static LastLoginAt of(LocalDateTime lastLogin) {
        if (lastLogin == null) {
            return empty();
        }
        if (lastLogin.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Last login time is after now");
        }
        return new LastLoginAt(lastLogin);
    }

    public static LastLoginAt empty() {
        return new LastLoginAt(null);
    }
}
