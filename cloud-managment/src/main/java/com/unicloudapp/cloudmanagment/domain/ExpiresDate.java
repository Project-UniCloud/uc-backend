package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpiresDate {

    LocalDate value;

    public static ExpiresDate of(LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date must be a future date");
        }
        return new ExpiresDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
