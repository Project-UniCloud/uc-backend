package com.unicloudapp.common.group;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupUniqueNameTest {

    @Test
    @DisplayName("fromString parses 'Name YYYYX' and toString reproduces it")
    void fromStringParsesCorrectly() {
        GroupUniqueName unique = GroupUniqueName.fromString("Grupa A 2024L");
        assertEquals("Grupa A", unique.groupName().toString());
        assertEquals("2024L", unique.semester().toString());
        assertEquals("Grupa A 2024L", unique.toString());
    }

    @Test
    @DisplayName("fromString throws on invalid format")
    void fromStringThrowsOnInvalid() {
        // null
        assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString(null));
        // missing space before suffix
        assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString("GrupaA2024L"));
        // wrong suffix pattern
        assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString("Grupa A 2024X"));
        // no digits
        assertThrows(IllegalArgumentException.class, () -> GroupUniqueName.fromString("Grupa A L"));
    }
}
