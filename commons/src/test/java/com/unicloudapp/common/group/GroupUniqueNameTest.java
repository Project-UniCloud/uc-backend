package com.unicloudapp.common.group;

import static org.junit.jupiter.api.Assertions.*;

import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GroupUniqueNameTest {

    @Test
    @DisplayName("fromString parses valid 'Name YYYYZ/L' and toString returns same format")
    void fromString_valid_thenOk_and_toString() {
        GroupUniqueName unique = GroupUniqueName.fromString("AI 2024L");
        assertEquals(GroupName.of("AI"), unique.groupName());
        assertEquals(Semester.of("2024L"), unique.semester());
        assertEquals("AI 2024L", unique.toString());
    }

    @Test
    @DisplayName("fromString throws for null")
    void fromString_null_thenThrows() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString(null));
        assertTrue(ex.getMessage().contains("Niepoprawny format"));
    }

    @Test
    @DisplayName("fromString throws for missing space")
    void fromString_missingSpace_thenThrows() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString("AI-2024L"));
        assertTrue(ex.getMessage().contains("Niepoprawny format"));
    }

    @Test
    @DisplayName("fromString throws for missing space")
    void fromStringWithoutSpaces() {
        GroupUniqueName unique = GroupUniqueName.fromStringWithoutSpaces("AI-2024L");
        assertEquals(GroupName.of("AI"), unique.groupName());
        assertEquals(Semester.of("2024L"), unique.semester());
        assertEquals("AI 2024L", unique.toString());
    }

    @Test
    @DisplayName("fromString throws for malformed suffix")
    void fromString_badSuffix_thenThrows() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString("AI 2024X"));
        assertTrue(ex.getMessage().contains("Niepoprawny format"));
    }
}
