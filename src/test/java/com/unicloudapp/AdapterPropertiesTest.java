package com.unicloudapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AdapterPropertiesTest {

    @Autowired
    private AdapterProperties adapterProperties;

    @Test
    public void shouldLoadAdapterPropertiesFromYml() {
        Map<String, AdapterProperties.AdapterConfig> clients = adapterProperties.clients();
        assertThat(clients).isNotEmpty();
        AdapterProperties.AdapterConfig adapter1 = clients.get("aws");
        assertThat(adapter1).isNotNull();
        assertThat(adapter1.host()).isEqualTo("localhost");
        assertThat(adapter1.name()).isEqualTo("AWS");
        assertThat(adapter1.port()).isEqualTo(50051);
    }
}

