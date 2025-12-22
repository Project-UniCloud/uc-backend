package com.unicloudapp.auth.application;

import com.unicloudapp.auth.application.port.in.AuthenticationUseCase;
import com.unicloudapp.common.vo.user.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AuthorizationService implements AuthenticationUseCase {

    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final JwtConfigurationProperties properties;

    @Override
    public AuthenticatedResult authenticate(String username, String password) {
        Authentication authenticated =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = (User) authenticated.getPrincipal();
        assert user != null;
        String token = buildToken(user);
        Set<UserRole.Type> roles = authenticated.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(r -> r.replace("ROLE_", ""))
                .map(UserRole.Type::valueOf)
                .collect(Collectors.toSet());
        return new AuthenticatedResult(token, UserRole.of(roles));
    }

    private String buildToken(User user) {
        Instant now = clock.instant();
        SecretKey key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(properties.expirationTimeInMs())))
                .signWith(key)
                .compact();
    }
}
