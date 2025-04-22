package com.unicloudapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdapterClientFactory {

    @Bean
    public Map<String, AdapterClient> adapterClients(AdapterProperties properties) {
        Map<String, AdapterClient> clients = new HashMap<>();

        for (Map.Entry<String, AdapterProperties.AdapterConfig> entry : properties.clients().entrySet()) {
            String name = entry.getKey();
            AdapterProperties.AdapterConfig config = entry.getValue();
            AdapterClient client = new AdapterClient(config.host(), config.port());
            clients.put(name, client);
        }

        return clients;
    }
}
