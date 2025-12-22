package com.unicloudapp.cloud.domain.vo;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

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

    public static ExpiresDate expirable(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Expiration date must be a future date");
        }
        return new ExpiresDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
