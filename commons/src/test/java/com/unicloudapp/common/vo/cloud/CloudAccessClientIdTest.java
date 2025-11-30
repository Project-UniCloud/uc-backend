package com.unicloudapp.common.vo.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudConnectorIdTest {

    @Test
    @DisplayName("of(null) and of(empty string) throw IllegalArgumentException")
    void invalidValuesThrow() {
        assertThrows(IllegalArgumentException.class, () -> CloudConnectorId.of(null));
        assertThrows(IllegalArgumentException.class, () -> CloudConnectorId.of(""));
    }

    @Test
    @DisplayName("of(non-empty) stores value and toString returns it")
    void validValue() {
        CloudConnectorId id1 = CloudConnectorId.of("client-1");
        CloudConnectorId id2 = CloudConnectorId.of("client-1");

        assertEquals("client-1", id1.id());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
