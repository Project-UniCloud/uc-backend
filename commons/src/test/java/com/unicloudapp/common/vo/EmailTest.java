package com.unicloudapp.common.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    @DisplayName("of(null) returns empty Email and isEmpty is true")
    void ofNullReturnsEmpty() {
        Email email = Email.of(null);
        assertTrue(email.isEmpty());
        assertEquals("", email.toString());
    }

    @Test
    @DisplayName("of(blank) returns empty Email and isEmpty is true")
    void ofBlankReturnsEmpty() {
        Email email = Email.of("   ");
        assertTrue(email.isEmpty());
        assertEquals("", email.toString());
    }

    @Test
    @DisplayName("of(valid) returns Email with value and not empty")
    void ofValidReturnsValue() {
        Email email = Email.of("john.doe@example.com");
        assertFalse(email.isEmpty());
        assertEquals("john.doe@example.com", email.toString());
    }

    @Test
    @DisplayName("of(invalid) throws IllegalArgumentException")
    void ofInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("john@@example.com"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("john@example"));
    }

    @Test
    @DisplayName("empty() returns empty Email and isEmpty is true")
    void emptyReturnsEmpty() {
        Email email = Email.empty();
        assertTrue(email.isEmpty());
        assertEquals("", email.toString());
    }
}
