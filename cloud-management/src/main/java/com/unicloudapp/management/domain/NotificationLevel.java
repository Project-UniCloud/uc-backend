package com.unicloudapp.management.domain;

public record NotificationLevel(Integer level) {

    public static NotificationLevel of(Integer level) {
        if (level == null || level < 0 || level > 100) {
            throw new IllegalArgumentException();
        }
        return new NotificationLevel(level);
    }
}
