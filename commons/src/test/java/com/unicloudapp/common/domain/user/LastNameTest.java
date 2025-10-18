package com.unicloudapp.common.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LastNameTest {

    @Test
    @DisplayName("of(blank) throws IllegalArgumentException")
    void blankThrows() {
        assertThrows(IllegalArgumentException.class, () -> LastName.of("   "));
    }

    @Test
    @DisplayName("of(null) throws NullPointerException due to implementation")
    void nullThrowsNpe() {
        assertThrows(NullPointerException.class, () -> LastName.of(null));
    }

    @Test
    @DisplayName("of(value) stores value and toString returns it")
    void storesValue() {
        LastName ln1 = LastName.of("Kowalski");
        LastName ln2 = LastName.of("Kowalski");

        assertEquals("Kowalski", ln1.toString());
        assertEquals(ln1, ln2);
        assertEquals(ln1.hashCode(), ln2.hashCode());
    }
}
