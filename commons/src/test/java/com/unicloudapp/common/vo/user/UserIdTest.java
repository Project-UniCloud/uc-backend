package com.unicloudapp.common.vo.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserIdTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserId.of(null));
    }

    @Test
    @DisplayName("of(UUID) stores value, toString returns UUID string, equality works")
    void storesUuid() {
        UUID id = UUID.randomUUID();
        UserId u1 = UserId.of(id);
        UserId u2 = UserId.of(id);

        assertEquals(id.toString(), u1.toString());
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
