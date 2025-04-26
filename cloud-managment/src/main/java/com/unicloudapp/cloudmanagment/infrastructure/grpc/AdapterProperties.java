package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import java.util.Map;

public record AdapterProperties(Map<String, AdapterConfig> clients) {

    public record AdapterConfig(String name, String host, int port) {}
}
