package com.unicloudapp.common.vo.cloud;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CostLimitTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> CostLimit.of(null));
    }

    @Test
    @DisplayName("of(negative) throws IllegalArgumentException")
    void ofNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> CostLimit.of(new BigDecimal("-1")));
    }

    @Test
    @DisplayName("of(0) and of(positive) create value objects")
    void ofZeroAndPositiveOk() {
        CostLimit zero = CostLimit.of(BigDecimal.ZERO);
        CostLimit positive = CostLimit.of(new BigDecimal("99.99"));

        assertEquals(0, zero.getCost().compareTo(BigDecimal.ZERO));
        assertEquals(0, positive.getCost().compareTo(new BigDecimal("99.99")));

        assertEquals(CostLimit.of(new BigDecimal("99.99")), positive);
        assertEquals(CostLimit.of(new BigDecimal("99.99")).hashCode(), positive.hashCode());
    }

    @Test
    @DisplayName("zero() returns zero cost")
    void zeroReturnsZero() {
        CostLimit zero = CostLimit.zero();
        assertEquals(0, zero.getCost().compareTo(BigDecimal.ZERO));
    }
}
