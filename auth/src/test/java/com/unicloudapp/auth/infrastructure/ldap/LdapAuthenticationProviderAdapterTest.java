package com.unicloudapp.auth.infrastructure.ldap;

import com.unicloudapp.auth.application.AdminProperties;
import com.unicloudapp.auth.application.LdapProperties;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserFullNameAndLoginProjection;
import com.unicloudapp.common.user.UserQueryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.naming.directory.DirContext;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LdapAuthenticationProviderAdapterTest {

    private UserQueryService userQueryService;
    private UserCommandService userCommandService;
    private AdminProperties adminProperties;
    private LdapTemplate ldapTemplate;
    private LdapProperties ldapProperties;

    @BeforeEach
    void setUp() {
        userQueryService = mock(UserQueryService.class);
        userCommandService = mock(UserCommandService.class);
        adminProperties = new AdminProperties(Collections.emptyList());
        ldapTemplate = mock(LdapTemplate.class);
        ldapProperties = new LdapProperties(
                "labs.wmi.amu.edu.pl",
                "DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl",
                "OU=Faculty,OU=People",
                "ldaps://dc1-2016.labs.wmi.amu.edu.pl:636"
        );
        // defaults in LdapProperties already match previous constants
    }

    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_whenLdapUnavailable_returnsNull_andDoesNotCreateUser() {
        // Given LDAP is unavailable
        when(ldapTemplate.getContextSource()).thenThrow(new RuntimeException("down"));

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
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

        // And LDAP is unavailable
        when(ldapTemplate.getContextSource()).thenThrow(new RuntimeException("down"));

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        List<?> result = adapter.searchLecturers("smith");

        // Then
        assertNotNull(result, "Result list should not be null");
        assertTrue(result.isEmpty(), "Expected empty list on LDAP failure");
        // Ensure we didn't query local user service when LDAP search failed immediately
        verifyNoInteractions(userQueryService);
    }

    @Test
    void authenticate_asAdmin_returnsAdmin_andDoesNotCreateWhenExists() {
        // Given
        ContextSource cs = mock(ContextSource.class);
        when(ldapTemplate.getContextSource()).thenReturn(cs);
        when(cs.getContext(anyString(), anyString())).thenReturn(mock(DirContext.class));
        // LDAP search returns one user
        when(ldapTemplate.search(anyString(), anyString(), (org.springframework.ldap.core.ContextMapper) any())).thenAnswer(inv -> {
            org.springframework.ldap.core.ContextMapper<?> mapper = inv.getArgument(2);
            DirContextAdapter entry = ldapEntry(
                    "CN=Admin User,OU=People,DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl",
                    "admin1", "Admin", "User", "admin1@labs.wmi.amu.edu.pl");
            Object mapped = mapper.mapFromContext(entry);
            return java.util.List.of(mapped);
        });
        when(userQueryService.existsByLogin("admin1")).thenReturn(true);

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                new AdminProperties(java.util.List.of("admin1")),
                ldapTemplate,
                ldapProperties
        );

        // When
        UserRole role = adapter.authenticate("admin1", "pass");

        // Then
        assertNotNull(role);
        assertEquals(UserRole.of(UserRole.Type.ADMIN), role);
        verify(userCommandService, never()).createUser(any());
    }

    @Test
    void authenticate_facultyUser_createsUser_andReturnsLecturer() {
        // Given
        ContextSource cs = mock(ContextSource.class);
        when(ldapTemplate.getContextSource()).thenReturn(cs);
        when(cs.getContext(anyString(), anyString())).thenReturn(mock(DirContext.class));
        when(ldapTemplate.search(anyString(), anyString(), (org.springframework.ldap.core.ContextMapper) any())).thenAnswer(inv -> {
            org.springframework.ldap.core.ContextMapper<?> mapper = inv.getArgument(2);
            // No email attribute to test null-safe extraction
            DirContextAdapter entry = ldapEntry(
                    "CN=John Doe,OU=Faculty,OU=People,DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl",
                    "jdoe", "John", "Doe", null);
            Object mapped = mapper.mapFromContext(entry);
            return java.util.List.of(mapped);
        });
        when(userQueryService.existsByLogin("jdoe")).thenReturn(false);

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        UserRole role = adapter.authenticate("jdoe", "secret");

        // Then
        assertNotNull(role);
        assertEquals(UserRole.of(UserRole.Type.LECTURER), role);
        verify(userCommandService, times(1)).createUser(any());
    }

    @Test
    void authenticate_userNotFound_afterSuccessfulBind_returnsNull() {
        // Given
        ContextSource cs = mock(ContextSource.class);
        when(ldapTemplate.getContextSource()).thenReturn(cs);
        when(cs.getContext(anyString(), anyString())).thenReturn(mock(DirContext.class));
        when(ldapTemplate.search(anyString(), anyString(), (org.springframework.ldap.core.ContextMapper) any())).thenAnswer(inv -> java.util.Collections.emptyList());

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        UserRole role = adapter.authenticate("nouser", "pass");

        // Then
        assertNull(role);
        verify(userCommandService, never()).createUser(any());
    }

    @Test
    void searchLecturers_success_returnsMappedProjections_withNullUuidWhenUserNotLocal() {
        // Given security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.getCredentials()).thenReturn("pass");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ContextSource cs = mock(ContextSource.class);
        when(ldapTemplate.getContextSource()).thenReturn(cs);
        when(cs.getContext(anyString(), anyString())).thenReturn(mock(DirContext.class));
        when(ldapTemplate.search(anyString(), anyString(), (org.springframework.ldap.core.ContextMapper) any())).thenAnswer(inv -> {
            org.springframework.ldap.core.ContextMapper<?> mapper = inv.getArgument(2);
            DirContextAdapter entry = ldapEntry(
                    "CN=John Doe,OU=Faculty,OU=People,DC=labs,DC=wmi,DC=amu,DC=edu,DC=pl",
                    "jdoe", "John", "Doe", "john@example.com");
            Object mapped = mapper.mapFromContext(entry);
            return java.util.List.of(mapped);
        });
        when(userQueryService.existsByLogin("jdoe")).thenReturn(false);

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        List<UserFullNameAndLoginProjection> list = adapter.searchLecturers("john");

        // Then
        assertEquals(1, list.size());
        UserFullNameAndLoginProjection p = list.getFirst();
        assertNull(p.getUuid());
        assertEquals("jdoe", p.getLogin());
        assertEquals("John", p.getFirstName());
        assertEquals("Doe", p.getLastName());
        assertEquals("john@example.com", p.getEmail());
    }

    @Test
    void searchLecturers_noSecurityContext_returnsEmpty() {
        // Given no security context
        SecurityContextHolder.clearContext();

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        List<UserFullNameAndLoginProjection> list = adapter.searchLecturers("x");

        // Then
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void searchLecturers_nullCredentials_returnsEmpty() {
        // Given context with null credentials
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.getCredentials()).thenReturn(null);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        LdapAuthenticationProviderAdapter adapter = new LdapAuthenticationProviderAdapter(
                userQueryService,
                userCommandService,
                adminProperties,
                ldapTemplate,
                ldapProperties
        );

        // When
        List<UserFullNameAndLoginProjection> list = adapter.searchLecturers("x");

        // Then
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // Helper to build a DirContextAdapter with attributes used by mapper
    private static DirContextAdapter ldapEntry(String dn, String login, String first, String last, String email) {
        DirContextAdapter a = new DirContextAdapter(dn);
        if (login != null) a.setAttributeValue("sAMAccountName", login);
        if (first != null) a.setAttributeValue("givenName", first);
        if (last != null) a.setAttributeValue("sn", last);
        if (email != null) a.setAttributeValue("mail", email);
        return a;
    }
}
