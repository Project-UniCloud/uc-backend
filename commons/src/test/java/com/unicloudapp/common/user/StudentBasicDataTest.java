package com.unicloudapp.common.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentBasicDataTest {

    @Test
    @DisplayName("Builder and getters/setters work as expected")
    void builderAndAccessors() {
        StudentBasicData data = StudentBasicData.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .login("s12345")
                .email("jan.kowalski@example.com")
                .build();

        assertEquals("Jan", data.getFirstName());
        assertEquals("Kowalski", data.getLastName());
        assertEquals("s12345", data.getLogin());
        assertEquals("jan.kowalski@example.com", data.getEmail());

        // test setters produced by Lombok
        data.setFirstName("Anna");
        data.setLastName("Nowak");
        data.setLogin("s54321");
        data.setEmail("anna.nowak@example.com");

        assertEquals("Anna", data.getFirstName());
        assertEquals("Nowak", data.getLastName());
        assertEquals("s54321", data.getLogin());
        assertEquals("anna.nowak@example.com", data.getEmail());
    }
}
