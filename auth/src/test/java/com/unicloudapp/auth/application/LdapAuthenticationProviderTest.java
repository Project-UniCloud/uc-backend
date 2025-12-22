package com.unicloudapp.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.vo.user.UserRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LdapAuthenticationProviderTest {

    @Mock
    private AuthenticationProviderPort ldapProvider;

    @Mock
    private AdminConfigurationProperties adminConfigurationProperties;

    private LdapAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        authenticationProvider = new LdapAuthenticationProvider(ldapProvider, adminConfigurationProperties);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        // given
        String username = "testuser";
        String password = "password";
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        UserRole userRole = UserRole.of(UserRole.Type.STUDENT);

        when(ldapProvider.authenticate(username, password)).thenReturn(userRole);
        when(adminConfigurationProperties.admins()).thenReturn(List.of("admin"));

        // when
        Authentication result = authenticationProvider.authenticate(authentication);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getName()).isEqualTo(username);
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_STUDENT");
    }

    @Test
    void shouldAuthenticateSuccessfullyWithAdminRole() {
        // given
        String username = "admin";
        String password = "password";
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        UserRole userRole = UserRole.of(UserRole.Type.LECTURER);

        when(ldapProvider.authenticate(username, password)).thenReturn(userRole);
        when(adminConfigurationProperties.admins()).thenReturn(List.of("admin"));

        // when
        Authentication result = authenticationProvider.authenticate(authentication);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_LECTURER", "ROLE_ADMIN");
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenLdapFails() {
        // given
        String username = "testuser";
        String password = "wrongpassword";
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

        when(ldapProvider.authenticate(username, password)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> authenticationProvider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("LDAP authentication failed");
    }

    @Test
    void shouldSupportUsernamePasswordAuthenticationToken() {
        assertThat(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class))
                .isTrue();
    }

    @Test
    void shouldNotSupportOtherAuthenticationTokens() {
        assertThat(authenticationProvider.supports(Authentication.class)).isFalse();
    }
}
