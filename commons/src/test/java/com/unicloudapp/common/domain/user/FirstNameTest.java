package com.unicloudapp.common.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstNameTest {

    @Test
    @DisplayName("of(blank) throws IllegalArgumentException")
    void blankThrows() {
        assertThrows(IllegalArgumentException.class, () -> FirstName.of("   "));
    }

    @Test
    @DisplayName("of(null) throws NullPointerException due to implementation")
    void nullThrowsNpe() {
        assertThrows(NullPointerException.class, () -> FirstName.of(null));
    }

    @Test
    @DisplayName("of(value) stores value and toString returns it")
    void storesValue() {
        FirstName fn1 = FirstName.of("Jan");
        FirstName fn2 = FirstName.of("Jan");

        assertEquals("Jan", fn1.toString());
        assertEquals(fn1, fn2);
        assertEquals(fn1.hashCode(), fn2.hashCode());
    }
}
