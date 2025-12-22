package com.unicloudapp.common.vo.cloud;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CloudResourceTypeTest {

    @Test
    @DisplayName("of(name) stores value and toString returns it")
    void ofStoresValue() {
        CloudResourceType t1 = CloudResourceType.of("VM");
        CloudResourceType t2 = CloudResourceType.of("VM");

        assertEquals("VM", t1.toString());
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
