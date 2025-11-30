package com.unicloudapp.common.vo.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudVendorConnectorIdTest {

    @Test
    @DisplayName("of(null) and of(empty string) throw IllegalArgumentException")
    void invalidValuesThrow() {
        assertThrows(IllegalArgumentException.class, () -> CloudVendorConnectorId.of(null));
        assertThrows(IllegalArgumentException.class, () -> CloudVendorConnectorId.of(""));
    }

    @Test
    @DisplayName("of(non-empty) stores value and toString returns it")
    void validValue() {
        CloudVendorConnectorId id1 = CloudVendorConnectorId.of("client-1");
        CloudVendorConnectorId id2 = CloudVendorConnectorId.of("client-1");

        assertEquals("client-1", id1.id());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
