package com.unicloudapp.cloudmanagment.domain;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class CostLimit {

    BigDecimal cost;

    public static CostLimit of(BigDecimal cost) {
        return new CostLimit(cost);
    }
}
