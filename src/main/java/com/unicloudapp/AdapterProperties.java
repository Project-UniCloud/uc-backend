package com.unicloudapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("adapters")
public record AdapterProperties(Map<String, AdapterConfig> clients) {

    public record AdapterConfig(String name, String host, int port) {}
}
