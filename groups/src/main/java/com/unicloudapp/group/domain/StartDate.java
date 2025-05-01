package com.unicloudapp.group.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class StartDate {

    LocalDateTime value;

    public static StartDate of(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        return new StartDate(value);
    }
}
