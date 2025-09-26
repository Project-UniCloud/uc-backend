package com.unicloudapp.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth")
public record AdminProperties(List<String> admins) { }
