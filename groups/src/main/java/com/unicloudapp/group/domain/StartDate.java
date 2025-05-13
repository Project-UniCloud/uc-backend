package com.unicloudapp.group.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
public class StartDate {

    LocalDate value;

    public static StartDate of(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        return new StartDate(value);
    }
}
