package com.unicloudapp.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.jwt")
record JwtConfigurationProperties(
        String secret,
        long expirationTimeInMs,
        String issuer
) {
}
