package com.unicloudapp.group.domain;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class StartDate {

    LocalDate value;

    public static StartDate of(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        return new StartDate(value);
    }
}
