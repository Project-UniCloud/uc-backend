package com.unicloudapp.cloudmanagment.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("adapters")
public record CloudAccessClientProperties(Map<String, SingleCloudAccessClientProperties> clients) {

    public record SingleCloudAccessClientProperties(
            String name,
            String host,
            int port,
            BigDecimal costLimit,
            String cronExpression,
            List<String> resourceTypes
    ) {}
}
