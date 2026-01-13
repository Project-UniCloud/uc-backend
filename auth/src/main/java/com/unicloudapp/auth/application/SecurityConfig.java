package com.unicloudapp.auth.application;

import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.user.UserLogin;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService,
            JwtConfigurationProperties jwtProperties,
            CorsConfigurationSource corsConfigurationSource,
            AuthCookieConfigurationProperties authCookieConfigurationProperties) {
        return http.csrf(AbstractHttpConfigurer::disable) // NOSONAR - Using stateless JWT authentication
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/auth", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                new JwtTokenParser(jwtProperties.secret()),
                                userDetailsService,
                                new JwtValidator(jwtProperties.secret())),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new CookieClearingAuthenticationEntryPoint(authCookieConfigurationProperties)))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(LdapAuthenticationProvider ldapAuthProvider) {
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
    UserDetailsService userDetailsService(UserQueryService userQueryService) {
        return username -> {
            UserDetails userDetails = userQueryService
                    .getUserDetailsByUsername(UserLogin.of(username))
                    .orElseThrow();
            return User.builder()
                    .username(userDetails.login().getValue())
                    .roles(userDetails.roles().getRoles().stream()
                            .map(Enum::name)
                            .toArray(String[]::new))
                    .password("")
                    .build();
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${ORIGIN:http://localhost:3000}") String originEnv) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(originEnv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
