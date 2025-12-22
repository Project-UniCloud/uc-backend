package com.unicloudapp.common.vo.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LastLoginAtTest {

    @Test
    @DisplayName("of(null) returns empty; isEmpty is true")
    void ofNullReturnsEmpty() {
        LastLoginAt last = LastLoginAt.of(null);
        assertTrue(last.isEmpty());
    }

    @Test
    @DisplayName("of(past) returns value; isEmpty false; toString may be default")
    void ofPastOk() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(1);
        LastLoginAt last = LastLoginAt.of(past);
        assertFalse(last.isEmpty());
        assertEquals(past, last.getValue());
    }

    @Test
    @DisplayName("of(future) throws IllegalArgumentException")
    void ofFutureThrows() {
        LocalDateTime future = LocalDateTime.now().plusSeconds(2);
        assertThrows(IllegalArgumentException.class, () -> LastLoginAt.of(future));
    }

    @Test
    @DisplayName("isAfter respects nulls and compares underlying values")
    void isAfterLogic() {
        LastLoginAt empty = LastLoginAt.of(null);
        LastLoginAt t1 = LastLoginAt.of(LocalDateTime.now().minusMinutes(10));
        LastLoginAt t2 = LastLoginAt.of(LocalDateTime.now().minusMinutes(5));

        assertFalse(empty.isAfter(null));
        assertFalse(empty.isAfter(t1));
        assertFalse(t1.isAfter(null));
        assertTrue(t2.isAfter(t1));
        assertFalse(t1.isAfter(t2));
    }
}
