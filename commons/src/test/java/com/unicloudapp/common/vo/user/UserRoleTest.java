package com.unicloudapp.common.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.of((UserRole.Type[]) null));
    }

    @Test
    @DisplayName("of(empty array) throws IllegalArgumentException")
    void ofEmptyArrayThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.of(new UserRole.Type[0]));
    }

    @Test
    @DisplayName("of(null Set) throws IllegalArgumentException")
    void ofNullSetThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.of((Set<UserRole.Type>) null));
    }

    @Test
    @DisplayName("of(empty Set) throws IllegalArgumentException")
    void ofEmptySetThrows() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.of(Collections.emptySet()));
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

    @Test
    @DisplayName("of(Set) stores values correctly")
    void ofSetOk() {
        Set<UserRole.Type> roles = Set.of(UserRole.Type.ADMIN, UserRole.Type.STUDENT);
        UserRole userRole = UserRole.of(roles);

        assertThat(userRole.getRoles()).containsExactlyInAnyOrder(UserRole.Type.ADMIN, UserRole.Type.STUDENT);
    }

    @Test
    @DisplayName("hasRole returns true when role is present")
    void hasRoleReturnsTrueWhenPresent() {
        UserRole userRole = UserRole.of(UserRole.Type.ADMIN, UserRole.Type.LECTURER);

        assertTrue(userRole.hasRole(UserRole.Type.ADMIN));
        assertTrue(userRole.hasRole(UserRole.Type.LECTURER));
    }

    @Test
    @DisplayName("hasRole returns false when role is absent")
    void hasRoleReturnsFalseWhenAbsent() {
        UserRole userRole = UserRole.of(UserRole.Type.STUDENT);

        assertFalse(userRole.hasRole(UserRole.Type.ADMIN));
        assertFalse(userRole.hasRole(UserRole.Type.LECTURER));
    }
}
