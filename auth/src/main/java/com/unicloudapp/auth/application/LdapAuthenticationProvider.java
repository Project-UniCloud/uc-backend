package com.unicloudapp.auth.application;

import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.vo.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class LdapAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationProviderPort ldapProvider;
    private final AdminConfigurationProperties adminConfigurationProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserRole userRole = ldapProvider.authenticate(username, password);
        if (userRole != null) {
            List<String> roles = userRole.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
            if (adminConfigurationProperties.admins().contains(username)) {
                roles.add("ADMIN");
            }
            org.springframework.security.core.userdetails.UserDetails user = User.builder()
                    .username(username)
                    .password(password)
                    .roles(roles.toArray(new String[0]))
                    .build();
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        throw new BadCredentialsException("LDAP authentication failed");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
