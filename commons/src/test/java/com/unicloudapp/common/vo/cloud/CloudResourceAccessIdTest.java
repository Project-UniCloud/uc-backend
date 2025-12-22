package com.unicloudapp.common.vo.cloud;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CloudResourceAccessIdTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> CloudResourceAccessId.of(null));
    }

    @Test
    @DisplayName("of(UUID) stores value and supports equality")
    void ofUuidOk() {
        UUID id = UUID.randomUUID();
        CloudResourceAccessId c1 = CloudResourceAccessId.of(id);
        CloudResourceAccessId c2 = CloudResourceAccessId.of(id);

        assertEquals(id, c1.getValue());
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
