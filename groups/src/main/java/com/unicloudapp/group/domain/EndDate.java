package com.unicloudapp.group.domain;

import lombok.Value;

import java.time.LocalDate;

@Value
public class EndDate {

    LocalDate value;

    public static EndDate of(LocalDate value) {
        return new EndDate(value);
    }
}
