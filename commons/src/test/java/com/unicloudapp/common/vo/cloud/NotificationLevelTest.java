package com.unicloudapp.common.vo.cloud;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NotificationLevelTest {

    @Test
    @DisplayName("of(null) throws IllegalArgumentException")
    void ofNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> NotificationLevel.of(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    @DisplayName("of(invalid) throws IllegalArgumentException")
    void ofInvalidThrows(int level) {
        assertThrows(IllegalArgumentException.class, () -> NotificationLevel.of(level));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 50, 100})
    @DisplayName("of(valid) creates NotificationLevel")
    void ofValidOk(int level) {
        NotificationLevel notificationLevel = NotificationLevel.of(level);
        assertEquals(level, notificationLevel.level());
    }

    @Test
    @DisplayName("NotificationLevel is a record and has equality based on level")
    void equalityTest() {
        NotificationLevel level1 = NotificationLevel.of(50);
        NotificationLevel level2 = NotificationLevel.of(50);
        NotificationLevel level3 = NotificationLevel.of(51);

        assertEquals(level1, level2);
        assertNotEquals(level1, level3);
        assertEquals(level1.hashCode(), level2.hashCode());
    }
}
