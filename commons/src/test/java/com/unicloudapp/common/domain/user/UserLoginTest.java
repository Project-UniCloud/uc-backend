package com.unicloudapp.common.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginTest {

    @Test
    @DisplayName("of(null) and of(blank) throw IllegalArgumentException")
    void invalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserLogin.of(null));
        assertThrows(IllegalArgumentException.class, () -> UserLogin.of("   "));
    }

    @Test
    @DisplayName("of(value) stores value and toString returns it")
    void storesValue() {
        UserLogin l1 = UserLogin.of("s12345");
        UserLogin l2 = UserLogin.of("s12345");

        assertEquals("s12345", l1.toString());
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }
}
