package com.unicloudapp.common.group;

import static org.junit.jupiter.api.Assertions.*;

import com.unicloudapp.common.vo.group.Semester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SemesterTest {

    @Test
    @DisplayName("of() parses valid semester and toString returns original format")
    void parsesValidSemester() {
        Semester summer = Semester.of("2024L");
        assertEquals("2024L", summer.toString());

        Semester winter = Semester.of("2023Z");
        assertEquals("2023Z", winter.toString());
    }

    @Test
    @DisplayName("of() throws on null, empty, or wrong length")
    void throwsOnInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> Semester.of(null));
        assertThrows(IllegalArgumentException.class, () -> Semester.of(""));
        assertThrows(IllegalArgumentException.class, () -> Semester.of("2024"));
        assertThrows(IllegalArgumentException.class, () -> Semester.of("20241X"));
    }
}
