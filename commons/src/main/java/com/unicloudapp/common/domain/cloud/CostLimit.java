package com.unicloudapp.common.domain.cloud;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CostLimit {

    BigDecimal cost;

    public static CostLimit of(BigDecimal cost) {
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost cannot be null or negative");
        }
        return new CostLimit(cost);
    }

    public static CostLimit zero() {
        return new CostLimit(BigDecimal.ZERO);
    }
}
