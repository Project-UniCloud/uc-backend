package com.unicloudapp.user.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserRoleTest {

    @Test
    void shouldCreateUserRoleWithValidType() {
        UserRole role = UserRole.of(UserRole.Type.ADMIN);

        assertThat(role).isNotNull();
        assertThat(role.getUserRoleType()).isEqualTo(UserRole.Type.ADMIN);
    }

    @ParameterizedTest
    @EnumSource(UserRole.Type.class)
    void shouldCreateUserRoleForAllValidTypes(UserRole.Type type) {
        UserRole role = UserRole.of(type);

        assertThat(role.getUserRoleType()).isEqualTo(type);
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThatThrownBy(() -> UserRole.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}