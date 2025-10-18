package com.unicloudapp.common.domain.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudAccessClientIdTest {

    @Test
    @DisplayName("of(null) and of(empty string) throw IllegalArgumentException")
    void invalidValuesThrow() {
        assertThrows(IllegalArgumentException.class, () -> CloudAccessClientId.of(null));
        assertThrows(IllegalArgumentException.class, () -> CloudAccessClientId.of(""));
    }

    @Test
    @DisplayName("of(non-empty) stores value and toString returns it")
    void validValue() {
        CloudAccessClientId id1 = CloudAccessClientId.of("client-1");
        CloudAccessClientId id2 = CloudAccessClientId.of("client-1");

        assertEquals("client-1", id1.toString());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
