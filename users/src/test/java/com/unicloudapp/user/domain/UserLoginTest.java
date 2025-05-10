package com.unicloudapp.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserLoginTest {

    @Test
    void should_throw_an_exception() {
        // given
        String value = null;

        // when && then
        assertThatThrownBy(() -> UserLogin.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User login cannot be null or empty");
    }
}