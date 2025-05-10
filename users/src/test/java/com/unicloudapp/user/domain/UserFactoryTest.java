package com.unicloudapp.user.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserFactoryTest {

    private final UserFactory userFactory = new UserFactory();

    @Test
    void shouldCreateUserWithCorrectProperties() {
        UUID userId = UUID.randomUUID();
        String indexNumber = "indexNumber";
        String firstName = "firstName";
        String lastName = "lastName";
        UserRole.Type role = UserRole.Type.ADMIN;

        User user = userFactory.create(userId,
                indexNumber,
                firstName,
                lastName,
                role
        );

        assertThat(user.getUserId().getValue()).isEqualTo(userId);
        assertThat(user.getUserLogin().getValue()).isEqualTo(indexNumber);
        assertThat(user.getFirstName().getValue()).isEqualTo(firstName);
        assertThat(user.getLastName().getValue()).isEqualTo(lastName);
        assertThat(user.getUserRole().getValue()).isEqualTo(role);
        assertThat(user.getEmail().getValue()).isEmpty();
        assertThat(user.getLastLoginAt().getValue()).isNull();
    }

    @Test
    void shouldRestoreUserWithAllFields() {
        UUID userId = UUID.randomUUID();
        String indexNumber = "789012";
        String firstName = "Anna";
        String lastName = "Nowak";
        UserRole.Type role = UserRole.Type.ADMIN;
        String email = "anna.nowak@example.com";
        LocalDateTime lastLogin = LocalDateTime.of(2023, 12, 25, 10, 15);

        User user = userFactory.restore(
                userId,
                indexNumber,
                firstName,
                lastName,
                role,
                email,
                lastLogin
        );

        assertThat(user.getUserId().getValue()).isEqualTo(userId);
        assertThat(user.getUserLogin().getValue()).isEqualTo(indexNumber);
        assertThat(user.getFirstName().getValue()).isEqualTo(firstName);
        assertThat(user.getLastName().getValue()).isEqualTo(lastName);
        assertThat(user.getUserRole().getValue()).isEqualTo(role);
        assertThat(user.getEmail().getValue()).isEqualTo(email);
        assertThat(user.getLastLoginAt().getValue()).isEqualTo(lastLogin);
    }
}