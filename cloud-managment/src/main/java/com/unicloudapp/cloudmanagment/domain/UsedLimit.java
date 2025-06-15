package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsedLimit {

    BigDecimal value;

    static UsedLimit of(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Used limit must be a non-negative value");
        }
        return new UsedLimit(value);
    }

    static UsedLimit empty() {
        return new UsedLimit(BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
