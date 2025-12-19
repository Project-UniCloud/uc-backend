package com.unicloudapp.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.cookie")
public record AuthCookieConfigurationProperties(
        boolean secure,
        String sameSite
) {
}
