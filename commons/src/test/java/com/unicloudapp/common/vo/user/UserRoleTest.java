package com.unicloudapp.common.vo.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.of((UserRole.Type[]) null));
    }

    @Test
    @DisplayName("of(enum) stores value; enum contains expected constants")
    void ofEnumOk() {
        UserRole r1 = UserRole.of(UserRole.Type.ADMIN);
        UserRole r2 = UserRole.of(UserRole.Type.ADMIN);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // ensure enum types exist
        assertNotNull(UserRole.Type.valueOf("ADMIN"));
        assertNotNull(UserRole.Type.valueOf("STUDENT"));
        assertNotNull(UserRole.Type.valueOf("LECTURER"));
    }
}
