package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import java.util.HashMap;
import java.util.Map;

public class AdapterClientFactory {

    public Map<String, AdapterClientAdapter> adapterClients(AdapterProperties properties) {
        Map<String, AdapterClientAdapter> clients = new HashMap<>();

        for (Map.Entry<String, AdapterProperties.AdapterConfig> entry : properties.clients().entrySet()) {
            String name = entry.getKey();
            AdapterProperties.AdapterConfig config = entry.getValue();
            AdapterClientAdapter client = new AdapterClientAdapter(config.host(), config.port());
            clients.put(name, client);
        }

        return clients;
    }
}
