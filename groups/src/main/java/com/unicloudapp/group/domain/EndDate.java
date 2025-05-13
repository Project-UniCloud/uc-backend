package com.unicloudapp.group.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EndDate {

    LocalDate value;

    public static EndDate of(LocalDate value) {
        return new EndDate(value);
    }
}
