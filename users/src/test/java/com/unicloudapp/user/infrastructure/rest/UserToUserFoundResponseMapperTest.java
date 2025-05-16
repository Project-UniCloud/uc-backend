package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.common.domain.Email;
import com.unicloudapp.common.domain.FirstName;
import com.unicloudapp.common.domain.LastLoginAt;
import com.unicloudapp.common.domain.LastName;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserLogin;
import com.unicloudapp.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserToUserFoundResponseMapperTest {

    private UserToUserFoundResponseMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserToUserFoundResponseMapperImpl();
    }

    @Test
    @DisplayName("Powinien poprawnie zmapować User na UserDTO, gdy wszystkie pola są obecne")
    void toUserFoundResponse_whenUserHasAllFields_shouldMapCorrectly() {
        // Given
        UUID userIdVal = UUID.randomUUID();
        String loginVal = "testuser";
        String firstNameVal = "Test";
        String lastNameVal = "User";
        UserRole.Type roleVal = UserRole.Type.ADMIN;
        String emailVal = "test@example.com";
        LocalDateTime lastLoginVal = LocalDateTime.now();

        User user = Mockito.mock(User.class);
        when(user.getUserId()).thenReturn(UserId.of(userIdVal));
        when(user.getUserLogin()).thenReturn(UserLogin.of(loginVal));
        when(user.getFirstName()).thenReturn(FirstName.of(firstNameVal));
        when(user.getLastName()).thenReturn(LastName.of(lastNameVal));
        when(user.getUserRole()).thenReturn(UserRole.of(roleVal));
        when(user.getEmail()).thenReturn(Email.of(emailVal));
        when(user.getLastLoginAt()).thenReturn(LastLoginAt.of(lastLoginVal));

        // When
        UserFoundResponse result = mapper.toUserFoundResponse(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userIdVal);
        assertThat(result.login()).isEqualTo(loginVal);
        assertThat(result.firstName()).isEqualTo(firstNameVal);
        assertThat(result.lastName()).isEqualTo(lastNameVal);
        assertThat(result.userRole()).isEqualTo(roleVal);
        assertThat(result.email()).isEqualTo(emailVal);
        assertThat(result.lastLoginAt()).isEqualTo(lastLoginVal);
    }
}