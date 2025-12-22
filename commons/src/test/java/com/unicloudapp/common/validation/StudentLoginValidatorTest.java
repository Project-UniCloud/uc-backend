package com.unicloudapp.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentLoginValidatorTest {

    private final StudentLoginValidator validator = new StudentLoginValidator();

    @Test
    @DisplayName("isValid returns true only for strings starting with 's' followed by digits")
    void validatesStudentLoginPattern() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("S123", null));
        assertFalse(validator.isValid("student1", null));
        assertFalse(validator.isValid("s", null));
        assertFalse(validator.isValid("sabc", null));

        assertTrue(validator.isValid("s1", null));
        assertTrue(validator.isValid("s123456", null));
    }
}
