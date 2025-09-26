package com.unicloudapp.auth.application;

import com.unicloudapp.auth.application.port.out.AuthenticationProviderPort;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.domain.user.UserRole;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Clock;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService,
            JwtConfigurationProperties jwtProperties
    ) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                new JwtTokenParser(jwtProperties.secret()),
                                userDetailsService,
                                new JwtValidator(jwtProperties.secret())
                        ),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProviderPort ldapProvider,
            AdminProperties adminProperties
    ) {
        AuthenticationProvider ldapAuthProvider = new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                UserRole userRole = ldapProvider.authenticate(username, password);
                if (userRole != null) {
                    org.springframework.security.core.userdetails.UserDetails user = User.builder()
                            .username(username)
                            .password(password)
                            .roles(adminProperties.admins().contains(username) ? "ADMIN" : userRole.getValue().name())
                            .build();
                    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                }

                throw new BadCredentialsException("LDAP authentication failed");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
        return new ProviderManager(List.of(ldapAuthProvider));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
    
    @Bean
    UserDetailsService userDetailsService(
            UserQueryService userQueryService
    ) {
        return username -> {
            UserDetails userDetails = userQueryService.getUserDetailsByUsername(UserLogin.of(username)).orElseThrow();
            return User.builder()
                    .username(userDetails.login().getValue())
                    .roles(userDetails.role().getValue().name())
                    .password("")
                    .build();
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://unicloud.projektstudencki.pl"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
