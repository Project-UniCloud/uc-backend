package com.unicloudapp.cloudmanagment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CostLimitTest {

    @Test
    void of() {
        var minusOne = BigDecimal.valueOf(-1);
        assertThatThrownBy(() -> CostLimit.of(minusOne))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cost cannot be null or negative");
    }

    @Test
    void of2() {
        assertThatThrownBy(() -> CostLimit.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cost cannot be null or negative");
    }
}