package com.unicloudapp.auth.application;

import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import com.unicloudapp.auth.application.port.out.AuthenticationPort;
import com.unicloudapp.common.domain.user.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
class AuthorizationService implements AuthenticationUseCase {

    private final AuthenticationPort authenticationPort;
    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final JwtConfigurationProperties properties;

    @Override
    public AuthenticatedResult authenticate(String username, String password) {
        Authentication authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = (User) authenticated.getPrincipal();
        String token = buildToken(user);
        String role = authenticated.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .findFirst()
                .orElseThrow();
        return new AuthenticatedResult(token, UserRole.of(UserRole.Type.valueOf(role)));
    }


    private String buildToken(User user) {
        Instant now = clock.instant();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(properties.expirationTimeInMs())))
                .signWith(SignatureAlgorithm.HS256, properties.secret())
                .compact();
    }
}
