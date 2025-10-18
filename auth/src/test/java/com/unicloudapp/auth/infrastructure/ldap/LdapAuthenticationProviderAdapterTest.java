package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.AdminProperties;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserQueryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LdapAuthenticationProviderAdapterTest {

    private UserQueryService userQueryService;
    private UserCommandService userCommandService;
    private AdminProperties adminProperties;

    @BeforeEach
    void setUp() {
        userQueryService = mock(UserQueryService.class);
        userCommandService = mock(UserCommandService.class);
        adminProperties = new AdminProperties(Collections.emptyList());
    }

    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_whenLdapUnavailable_returnsNull_andDoesNotCreateUser() {
        // Given an adapter with no reachable LDAP (constructor will throw inside)
        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties
        );

        // When
        var result = adapter.authenticate("someUser", "somePass");

        // Then
        assertNull(result, "Expected null role when LDAP is unavailable or fails");
        // Ensure we didn't attempt any user creation in failure path
        verifyNoInteractions(userCommandService);
    }

    @Test
    void searchLecturers_whenLdapUnavailable_returnsEmptyList_andDoesNotQueryUserService() {
        // Given security context with some credentials
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.getCredentials()).thenReturn("pass");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties
        );

        // When
        List<?> result = adapter.searchLecturers("smith");

        // Then
        assertNotNull(result, "Result list should not be null");
        assertTrue(result.isEmpty(), "Expected empty list on LDAP failure");
        // Ensure we didn't query local user service when LDAP search failed immediately
        verifyNoInteractions(userQueryService);
    }
}
