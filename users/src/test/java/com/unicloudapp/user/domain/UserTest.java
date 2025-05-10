package com.unicloudapp.user.domain;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.LastLoginAt;
import com.unicloudapp.common.domain.LastName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.UserRole.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    /**
     * Test getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link User#logIn(LastLoginAt)}
     *   <li>{@link User#updateEmail(Email)}
     *   <li>{@link User#updateFirstName(FirstName)}
     *   <li>{@link User#updateLastName(LastName)}
     * </ul>
     */
    @Test
    @DisplayName("Test getters and setters")
    @MethodsUnderTest({"void User.logIn(LastLoginAt)", "void User.updateEmail(Email)",
            "void User.updateFirstName(FirstName)", "void User.updateLastName(LastName)"})
    void testGettersAndSetters() throws IllegalArgumentException {
        // Arrange
        UUID randomUUID = UUID.randomUUID();
        User user = User.builder()
                .userId(UserId.of(randomUUID))
                .userLogin(UserLogin.of("login"))
                .firstName(FirstName.of("firstName"))
                .lastName(LastName.of("lastName"))
                .email(Email.empty())
                .lastLoginAt(LastLoginAt.neverBeenLoggedIn())
                .userRole(UserRole.of(Type.ADMIN))
                .build();
        LastLoginAt lastLoginAt = LastLoginAt.of(LocalDateTime.now());
        Email newEmail = Email.of("test@example.pl");
        FirstName newFirstName = FirstName.of("newFirstName");
        LastName newLastName = LastName.of("newLastName");

        // Act
        user.logIn(lastLoginAt);
        user.updateEmail(newEmail);
        user.updateFirstName(newFirstName);
        user.updateLastName(newLastName);

        // Assert
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getFirstName()).isEqualTo(newFirstName);
        assertThat(user.getLastName()).isEqualTo(newLastName);
        assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
    }
}
