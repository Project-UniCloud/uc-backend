package com.unicloudapp.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class UserContextTest {

    private UserContext userContext;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        userContext = new UserContext();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return user login when authenticated")
    void shouldReturnUserLoginWhenAuthenticated() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // when
        String login = userContext.getCurrentUserLogin();

        // then
        assertEquals("testUser", login);
    }

    @Test
    @DisplayName("Should return 'system' when not authenticated")
    void shouldReturnSystemWhenNotAuthenticated() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // when
        String login = userContext.getCurrentUserLogin();

        // then
        assertEquals("system", login);
    }

    @Test
    @DisplayName("Should return 'system' when authentication is null")
    void shouldReturnSystemWhenAuthenticationIsNull() {
        // given
        when(securityContext.getAuthentication()).thenReturn(null);

        // when
        String login = userContext.getCurrentUserLogin();

        // then
        assertEquals("system", login);
    }
}
