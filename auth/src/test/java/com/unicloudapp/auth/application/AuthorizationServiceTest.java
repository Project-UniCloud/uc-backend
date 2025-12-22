package com.unicloudapp.auth.application;

import com.unicloudapp.common.vo.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtConfigurationProperties properties;

    private Clock clock;
    private AuthorizationService authorizationService;

    private static final String SECRET = "verysecretkeyverysecretkeyverysecretkeyverysecretkey";
    private static final long EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-12-22T20:00:00Z"), ZoneId.of("UTC"));
        authorizationService = new AuthorizationService(authenticationManager, clock, properties);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        // given
        String username = "testuser";
        String password = "password";
        User principal = new User(username, password, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(properties.secret()).thenReturn(SECRET);
        when(properties.expirationTimeInMs()).thenReturn(EXPIRATION);

        // when
        AuthenticatedResult result = authorizationService.authenticate(username, password);

        // then
        assertThat(result).isNotNull();
        assertThat(result.token()).isNotBlank();
        assertThat(result.roles().hasRole(UserRole.Type.STUDENT)).isTrue();
        assertThat(result.roles().getRoles()).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        // given
        String username = "testuser";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThatThrownBy(() -> authorizationService.authenticate(username, password))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");
    }

    @Test
    void shouldMapMultipleRolesCorrectly() {
        // given
        String username = "adminuser";
        String password = "password";
        User principal = new User(username, password, List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_LECTURER")
        ));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(properties.secret()).thenReturn(SECRET);
        when(properties.expirationTimeInMs()).thenReturn(EXPIRATION);

        // when
        AuthenticatedResult result = authorizationService.authenticate(username, password);

        // then
        assertThat(result.roles().hasRole(UserRole.Type.ADMIN)).isTrue();
        assertThat(result.roles().hasRole(UserRole.Type.LECTURER)).isTrue();
        assertThat(result.roles().getRoles()).hasSize(2);
    }
}
