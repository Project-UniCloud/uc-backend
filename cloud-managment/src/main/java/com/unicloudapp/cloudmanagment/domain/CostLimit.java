package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CostLimit {

    private final BigDecimal cost;

    public static CostLimit of(BigDecimal cost) {
        return new CostLimit(cost);
    }
}
