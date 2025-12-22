package com.unicloudapp.common.vo.cloud;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsedLimitTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> UsedLimit.of(null));
    }

    @Test
    @DisplayName("of(negative) throws IllegalArgumentException")
    void ofNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> UsedLimit.of(new BigDecimal("-0.01")));
    }

    @Test
    @DisplayName("of(0) and of(positive) create value objects; toString returns numeric string")
    void ofZeroAndPositiveOk() {
        UsedLimit zero = UsedLimit.of(BigDecimal.ZERO);
        UsedLimit positive = UsedLimit.of(new BigDecimal("12.34"));

        assertEquals("0", zero.toString());
        assertEquals("12.34", positive.toString());
        assertEquals(0, zero.getValue().compareTo(BigDecimal.ZERO));
        assertEquals(0, positive.getValue().compareTo(new BigDecimal("12.34")));

        // equality/hashCode from Lombok @Value
        assertEquals(UsedLimit.of(new BigDecimal("12.34")), positive);
        assertEquals(UsedLimit.of(new BigDecimal("12.34")).hashCode(), positive.hashCode());
    }

    @Test
    @DisplayName("empty() returns zero value")
    void emptyReturnsZero() {
        UsedLimit empty = UsedLimit.empty();
        assertEquals(0, empty.getValue().compareTo(BigDecimal.ZERO));
        assertEquals("0", empty.toString());
    }
}
