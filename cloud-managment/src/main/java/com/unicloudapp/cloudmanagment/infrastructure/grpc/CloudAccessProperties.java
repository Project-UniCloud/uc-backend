package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties("adapters")
record CloudAccessProperties(Map<String, SingleCloudAccessProperties> clients) {

    record SingleCloudAccessProperties(String name, String host, int port, BigDecimal costLimit, String cronExpression) {}
}
